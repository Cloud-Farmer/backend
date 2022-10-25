package SpringBoot.Codebase.config;

import SpringBoot.Codebase.domain.measurement.Cdc;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Soil;
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
                new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, MQTT_SUB_CLIENT_ID, TOPIC_FILTER);
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
            String kitId = token[0];
            if (token.length > 2) { // 1 이상이면 1/sensor/sensor 임
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
                } else if (sensor.equals("cdc")) {
                    Cdc cdc = new Cdc();
                    cdc.setKitId(kitId);
                    cdc.setValue(value);
                    sensorService.writeCdc(cdc);
                } else if (sensor.equals("soil")) {
                    Soil soil = new Soil();
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