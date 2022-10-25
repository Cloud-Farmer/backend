package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.measurement.Cdc;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Soil;
import SpringBoot.Codebase.domain.measurement.Temperature;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
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

    //private final InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086","admin","12345");

    public void sentToMqtt() {
        mqttOrderGateway.sendToMqtt("1", "1/actuator/motor");
    }

    public void writeTemperature(Temperature temperature) {
        Point point = Point.measurement("temperature")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("kit_id", temperature.getKitId())
                .addField("value", temperature.getValue())
                .build();
        influxDBTemplate.write(point);
    }

    public void writeHumidity(Humidity humidity) {
        Point point = Point.measurement("humidity")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("kit_id", humidity.getKitId())
                .addField("value", humidity.getValue())
                .build();
        influxDBTemplate.write(point);
    }

    public void writeCdc(Cdc cdc) {
        Point point = Point.measurement("cdc")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("kit_id", cdc.getKitId())
                .addField("value", cdc.getValue())
                .build();
        influxDBTemplate.write(point);
    }

    public void writeSoil(Soil soil) {
        Point point = Point.measurement("soil")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("kit_id", soil.getKitId())
                .addField("value", soil.getValue())
                .build();
        influxDBTemplate.write(point);
    }

    public void writeData() {
        for (int index = 1; index <= 1; index++) {
            Point point = Point.measurement("kit1")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("farmname", "test" + 4)
                    .addField("huminity", (float)(0.12+1))
                    .addField("moisture", (float)(0.23+2))
                    .addField("temperature",(float)(0.46+3))
                    .build();
            influxDBTemplate.write(point);
        }
    }

    public List<QueryResult.Result> selectDataFromSensor(String sensor, String limit) {
        String queryStr = String.format("SELECT * FROM %s LIMIT %s", sensor, limit);

        Query query = BoundParameterQuery.QueryBuilder.newQuery(queryStr)
                .forDatabase("smartfarm")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        return queryResult.getResults();
    }

}
