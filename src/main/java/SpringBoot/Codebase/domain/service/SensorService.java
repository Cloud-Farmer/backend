package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.entity.Actuator;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.measurement.SoilHumidity;
import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.repository.ActuatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    private final ActuatorRepository actuatorRepository;
    @Autowired
    public SensorService(InfluxDBTemplate<Point> influxDBTemplate, ActuatorRepository actuatorRepository) {
        this.influxDBTemplate = influxDBTemplate;
        this.actuatorRepository = actuatorRepository;
    }

    public void sentToMqtt(String kitId, String sensor, String available) {
        String topic = kitId + "/actuator/" + sensor;
        mqttOrderGateway.sendToMqtt(available, topic);
        log.info("topic {} data : {}", topic, available);
    }

    public boolean receivedToActuator(String kitId, String sensor) {
        Actuator find = actuatorRepository.findByKitIdAndSensorOrderByTimeDesc(Long.valueOf(kitId), sensor, PageRequest.of(0, 1))
                .stream().findFirst()
                .orElseThrow(() -> {
                    throw new RuntimeException("저장된 액츄에이터 상태가 없습니다");
                });

        return find.isStatus();
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

    public void writeCdc(Illuminance illuminance) {
        Point point = Point.measurement("illuminance")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("kit_id", illuminance.getKitId())
                .addField("value", illuminance.getValue())
                .build();
        influxDBTemplate.write(point);
    }

    public void writeSoil(SoilHumidity soil) {
        Point point = Point.measurement("soilhumidity")
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

    public void writeActuator(Actuator actuator) {
        actuatorRepository.save(actuator);
    }

    public List<QueryResult.Result> selectDataSensor(String kitId,String sensor, String date) {
        String queryStr = String.format("SELECT * FROM %s where kit_id = '%s' and time > now() - %s", sensor,kitId,date);


        Query query = BoundParameterQuery.QueryBuilder.newQuery(queryStr)
                .forDatabase("smartfarm")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        return queryResult.getResults();
    }
}