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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
@Service@Slf4j
public class SensorService {

    private final static Logger logger = LoggerFactory.getLogger(Sensordto.class.getSimpleName());
    @Autowired
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    private final InfluxDBTemplate<Point> influxDBTemplate;

    public SensorService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    public void sentToMqtt(String kitId, String sensor, String available) {
        String topic = kitId+ "/actuator/" + sensor;
        mqttOrderGateway.sendToMqtt(available, topic);
        log.info("topic: {} data : {}",topic,available);
    }
<<<<<<< HEAD
    public void writeData() {

        for (int index = 1; index <= 1; index++) {
            Point point = Point.measurement("smartfarm_db")
=======

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
>>>>>>> 9fcb0b52fb08da2b9a220ee559158639cd9cccd3
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

<<<<<<< HEAD
        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM smartfarm_db tz('Asia/Seoul')")
=======
    public List<Temperature> selectDataFromTemperature(String sensor) {

        Query query = BoundParameterQuery.QueryBuilder.newQuery(String.format("SELECT * FROM temperature tz('Asia/Seoul') "))
>>>>>>> 9fcb0b52fb08da2b9a220ee559158639cd9cccd3
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
