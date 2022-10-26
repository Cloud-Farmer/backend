package SpringBoot.Codebase.config;

import SpringBoot.Codebase.domain.entity.Actuator;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.measurement.SoilHumidity;
import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@IntegrationComponentScan
public class MqttConfiguration {

    private final String BROKER_URL;
    private final String MQTT_SUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private final String MQTT_PUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private final String TOPIC_FILTER;

    private final SensorService sensorService;

    @Autowired
    public MqttConfiguration(@Value("${mqtt.url}") String BROKER_URL,
                             @Value("${mqtt.port}") String PORT,
                             @Value("${mqtt.topic}") String TOPIC,
                             SensorService sensorService) {
        this.BROKER_URL = BROKER_URL + ":" + PORT;
        this.TOPIC_FILTER = TOPIC;
        this.sensorService = sensorService;
    }

    private MqttConnectOptions connectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
//        options.setUserName(MQTT_USERNAME);
//        options.setPassword(MQTT_PASSWORD.toCharArray());
        options.setServerURIs(new String[] { BROKER_URL });
        return options;
    }


    @Bean
    public DefaultMqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(connectOptions());
        return factory;
    }

    // 구독 세팅
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inboundChannel() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, MQTT_SUB_CLIENT_ID, TOPIC_FILTER, "1/#");   // 동적으로 구독 토픽 생성하기
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler inboundMessageHandler() {
        return message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
//            System.out.println("Topic:" + topic);
//            System.out.println("Payload " + message.getPayload());
            log.info("topic: " + topic + " payload: " + message.getPayload());
            // 수신 받은 데이터 InfluxDB에 적재
            String[] token = topic.split("/");
            String payload = message.getPayload().toString();
            String kitId = token[0];
            if (token[1].equals("actuator")) {
                String sensor = token[2];
                if (token.length == 3) {
                    log.info(topic); // status 저장하기
                    Actuator actuator = new Actuator();
                    boolean isActive = false;
                    if (payload.equals("1")) {
                        isActive = true;
                    }
                    actuator.setStatus(isActive);
                    actuator.setTime(LocalDateTime.now());
                    actuator.setSensor(sensor);
                    actuator.setKitId(Long.valueOf(kitId));
                    sensorService.writeActuator(actuator);
                }
            }
            else if (token[1].equals("sensor") && token.length > 2) {
                String sensor = token[2];
                String value = message.getPayload().toString();
                if (sensor.equals("temperature")) {
                    Temperature temperature = new Temperature();
                    temperature.setValue(value);
                    temperature.setKitId(kitId);
                    sensorService.writeTemperature(temperature);
                } else if (sensor.equals("humidity")) {
                    Humidity humidity = new Humidity();
                    humidity.setKitId(kitId);
                    humidity.setValue(value);
                    sensorService.writeHumidity(humidity);
                } else if (sensor.equals("illuminance")) {
                    Illuminance illuminance = new Illuminance();
                    illuminance.setKitId(kitId);
                    illuminance.setValue(value);
                    sensorService.writeCdc(illuminance);
                } else if (sensor.equals("soilhumidity")) {
                    SoilHumidity soil = new SoilHumidity();
                    soil.setKitId(kitId);
                    soil.setValue(value);
                    sensorService.writeSoil(soil);
                }
            }
        };
    }

    // 발행
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOrderMessageHandler() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(MQTT_PUB_CLIENT_ID, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(TOPIC_FILTER);
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttOrderGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }
}