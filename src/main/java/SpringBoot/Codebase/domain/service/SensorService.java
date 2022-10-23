package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.domain.dto.Sensordto;
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


@Service
public class SensorService {

    private final static Logger logger = LoggerFactory.getLogger(Sensordto.class.getSimpleName());
    private final InfluxDBTemplate<Point> influxDBTemplate;

    public SensorService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    //private final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086","admin","12345");

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

        for (int index = 1; index <= 5; index++) {

            Point point = Point.measurement("test_db")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("farmname", "test" + index)
                    .addField("huminity", (float)(0.12+index))
                    .addField("moisture", (float)(0.23+index))
                    .addField("temperature",(float)(0.46+index))
                    .build();

            influxDBTemplate.write(point);
        }
    }
    public List<Sensordto> SelectSensorData() {

        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM test_db")
                .forDatabase("test")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<Sensordto> testMeasurementList = resultMapper.toPOJO(queryResult, Sensordto.class);

        for (Sensordto tm : testMeasurementList) {
            logger.debug(tm.toString());
        }
        return testMeasurementList;
    }
}
