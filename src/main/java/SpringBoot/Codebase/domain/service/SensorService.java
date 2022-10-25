package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.measurement.Temperature;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;
import retrofit2.http.HEAD;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.messaging.Message;
@Service@Slf4j
public class SensorService {

    private final static Logger logger = LoggerFactory.getLogger(Sensordto.class.getSimpleName());
    @Autowired
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    private final InfluxDBTemplate<Point> influxDBTemplate;

    public SensorService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    //private final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086","admin","12345");

    public void sentToMqtt() {
        mqttOrderGateway.sendToMqtt("1", "1/actuator/motor");
    }
    @Autowired
    @ServiceActivator(inputChannel = "mqttInputChannel")
//    public MessageHandler inboundMessageHandler(
//            return message -> {
//        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
//        System.out.println("Topic:" + topic);
//        System.out.println("Payload" + message.getPayload());
//    };
//    )
    public void writeData() {
//        Pong response = this.influxDB.ping();
//        if (response.getVersion().equalsIgnoreCase("unknown")) {
//            //log.error("Error pinging server.")
//            return;
//        }
//        influxDB.createDatabase("dbtest");
//        influxDB.createRetentionPolicy(
//                "defaultPolicy", "baeldung", "30d", 1, true);

//        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
        /*try {
            MqttClient mqttClient = new MqttClient("tcp://192.168.0.37:1883","client1");
            mqttClient.connect();
            mqttClient.subscribe("cloudfarm/humidity");
            String hum = String.valueOf(mqttClient.getTopic("humidity"));

            log.info(hum);
        } catch (MqttException e) {
            e.printStackTrace();
        }*/
        for (int index = 1; index <= 1; index++) {
            Point point = Point.measurement("smartfarm_db")


    public void writeTemperature(Temperature temperature) {
        //influxDBTemplate.write(Point.measurementByPOJO(Temperature.class).addFieldsFromPOJO(temperature).build());
        Point point = Point.measurement("temperature")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("kitid", temperature.getKitId())
                .addField("value", temperature.getValue())
                .build();
        influxDBTemplate.write(point);
    }

    public void writeData() {
        for (int index = 1; index <= 1; index++) {
            Point point = Point.measurement("kit1")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("farmname", "test" + 4)
                    .addField("huminity", (float)(0.12+1))
                    .addField("cdc", (float)(0.23+2))
                    .addField("temperature",(float)(0.46+3))
                    .addField("soil",(float)(0.46+3))
                    .build();
            influxDBTemplate.write(point);
        }
    }
        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM smartfarm_db tz('Asia/Seoul')")

    public List<Temperature> selectDataFromTemperature(String sensor) {

        Query query = BoundParameterQuery.QueryBuilder.newQuery(String.format("SELECT * FROM temperature tz('Asia/Seoul') "))

                .forDatabase("smartfarm")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<Temperature> temperatures = resultMapper.toPOJO(queryResult, Temperature.class);

        for (Temperature data : temperatures) {
            log.info(data.toString());
        }
        return temperatures;
    }

}
