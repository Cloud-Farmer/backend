package SpringBoot.Codebase.config;

import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.service.SensorService;
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

@Configuration
@IntegrationComponentScan
public class MqttConfiguration {

    private final String BROKER_URL;
    private final String MQTT_SUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private final String MQTT_PUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private final String TOPIC1;
    private final String TOPIC2;

    private final SensorService sensorService;

    @Autowired
    public MqttConfiguration(@Value("${mqtt.url}") String BROKER_URL,
                             @Value("${mqtt.port}") String PORT,
                             @Value("${mqtt.topic1}") String TOPIC1,
                             @Value("${mqtt.topic2}") String TOPIC2, SensorService sensorService) {
        this.sensorService = sensorService;

        this.BROKER_URL = BROKER_URL + ":" + PORT;
        this.TOPIC1 = TOPIC1;
        this.TOPIC2 = TOPIC2;
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
                new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, MQTT_SUB_CLIENT_ID, TOPIC1, TOPIC2);
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
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttOrderGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }
}

// {id}/actuator?motor=1&pen=1 // query parmater
// {id}/actuator
// 1 : true, 0 : false
