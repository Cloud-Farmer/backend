package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
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
public class SensorService{

    @Autowired
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    private final static Logger logger = LoggerFactory.getLogger(Sensordto.class.getSimpleName());
    private final InfluxDBTemplate<Point> influxDBTemplate;

    public SensorService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    public void sentToMqtt(String kitId, String sensor, String available) {
        String topic = kitId+ "/actuator/" + sensor;
        mqttOrderGateway.sendToMqtt(available, topic);
        log.info("topic: {} data : {}",topic,available);
    }
    public void writeData() {

        for (int index = 1; index <= 1; index++) {
            Point point = Point.measurement("smartfarm_db")
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
    public List<Sensordto> SelectSensorData() {

        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM smartfarm_db tz('Asia/Seoul')")
                .forDatabase("smartfarm")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<Sensordto> testMeasurementList = resultMapper.toPOJO(queryResult, Sensordto.class);

        for (Sensordto tm : testMeasurementList) {
            System.err.println("tm. = " + tm.toString());
        }
        return testMeasurementList;
    }

}
