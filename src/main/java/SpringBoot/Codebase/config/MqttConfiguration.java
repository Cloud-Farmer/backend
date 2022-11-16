package SpringBoot.Codebase.config;

import SpringBoot.Codebase.domain.entity.Actuator;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.measurement.SoilHumidity;
import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.service.ActuatorService;
import SpringBoot.Codebase.domain.service.SensorService;
import SpringBoot.Codebase.util.AlertManager;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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


    private final ActuatorService actuatorService;
    private final SensorService sensorService;

    private final AlertManager alertManager;

    @Autowired
    public MqttConfiguration(@Value("${mqtt.url}") String BROKER_URL,
                             @Value("${mqtt.port}") String PORT,
                             @Value("${mqtt.topic}") String TOPIC,
                             ActuatorService actuatorService, SensorService sensorService, AlertManager alertManager) {
        this.alertManager = alertManager;
        this.BROKER_URL = BROKER_URL + ":" + PORT;
        this.TOPIC_FILTER = TOPIC;
        this.actuatorService = actuatorService;
        this.sensorService = sensorService;
    }

    private MqttConnectOptions connectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
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
                new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, MQTT_SUB_CLIENT_ID, TOPIC_FILTER, "1/json", "2/json");   // 동적으로 구독 토픽 생성하기
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
            log.info("topic: " + topic + " payload: " + message.getPayload());
            String[] token = topic.split("/");
            String payload = message.getPayload().toString();

            // 수신 받은 데이터 InfluxDB에 적재
            String kitId = token[0]; //kitId
            String type = token[1]; // actuator or sensor
            JSONParser parser = new JSONParser();
            try {
                JSONObject object = (JSONObject) parser.parse(payload);
                JSONObject sensor = (JSONObject) object.get("Sensor");
                JSONObject actuator= (JSONObject) object.get("Actuator");

                Temperature temperature = new Temperature();
                temperature.setValue(Float.valueOf(sensor.get("temperature").toString()));
                temperature.setKitId(kitId);
                sensorService.writeTemperature(temperature);

                Illuminance illuminance = new Illuminance();
                illuminance.setValue(Float.valueOf(sensor.get("illuminance").toString()));
                illuminance.setKitId(kitId);
                sensorService.writeCdc(illuminance);

                Humidity humidity = new Humidity();
                humidity.setValue(Float.valueOf(sensor.get("humidity").toString()));
                humidity.setKitId(kitId);
                sensorService.writeHumidity(humidity);

                SoilHumidity soilHumidity = new SoilHumidity();
                soilHumidity.setValue(Float.valueOf(sensor.get("soilhumidity").toString()));
                soilHumidity.setKitId(kitId);
                sensorService.writeSoil(soilHumidity);

                alertManager.run(temperature);
                alertManager.run(illuminance);
                alertManager.run(humidity);
                alertManager.run(soilHumidity);

                Actuator window = new Actuator();
                window.setSensor("window");
                window.setKitId(Long.valueOf(kitId));
                window.setTime(LocalDateTime.now());
                Long value = (Long) actuator.get(window.getSensor());
                window.setStatus(value == 1 ? true : false);
                sensorService.writeActuator(window);

                Actuator pump = new Actuator();
                pump.setSensor("pump");
                pump.setKitId(Long.valueOf(kitId));
                pump.setTime(LocalDateTime.now());
                value = (Long) actuator.get(pump.getSensor());
                pump.setStatus(value == 1 ? true : false);
                sensorService.writeActuator(pump);

                Actuator led = new Actuator();
                led.setSensor("led");
                led.setKitId(Long.valueOf(kitId));
                led.setTime(LocalDateTime.now());
                value = (Long) actuator.get(led.getSensor());
                led.setStatus(value == 1 ? true : false);
                sensorService.writeActuator(led);

                Actuator fan = new Actuator();
                fan.setSensor("fan");
                fan.setKitId(Long.valueOf(kitId));
                fan.setTime(LocalDateTime.now());
                value = (Long) actuator.get(fan.getSensor());
                fan.setStatus(value == 1 ? true : false);
                sensorService.writeActuator(fan);
            } catch (Exception e) {
                e.printStackTrace();
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
        messageHandler.setDefaultQos(2);
        messageHandler.setDefaultTopic(TOPIC_FILTER);
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttOrderGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }
}