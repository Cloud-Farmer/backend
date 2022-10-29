package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.dto.Sensordto;
import SpringBoot.Codebase.domain.entity.Actuator;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.measurement.SoilHumidity;
import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.repository.ActuatorRepository;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.influxdb.client.write.Point;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SensorService {

    private final static Logger logger = LoggerFactory.getLogger(Sensordto.class.getSimpleName());

    @Autowired
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    private char[] token = "90tcGlGZBFD-Er65i5a84lyeGqfrCz3MSkc1TuhAwA9E-YBTfntCpSmAayK-stq-UjVGb5v4SJNKYNTJhBZ_Yg==".toCharArray();
    private String org = "400c6a9a27d25d56";
    private String bucket = "test";
    private String url = "http://localhost:8086";
    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;
    private ZonedDateTime time;

    private  ActuatorRepository actuatorRepository;

    @Autowired
    public SensorService(ActuatorRepository actuatorRepository) {
        this.actuatorRepository = actuatorRepository;
        this.influxDBClient =InfluxDBClientFactory.create(url, token, org, bucket);
        this.writeApi=influxDBClient.getWriteApiBlocking();
        this.time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));
    }

    public void sentToMqtt(String kitId, String sensor, String available) {
        String topic = kitId + "/actuator/" + sensor;
        mqttOrderGateway.sendToMqtt(available, topic);
        logger.info("topic {} data : {}", topic, available);
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
        Point point = com.influxdb.client.write.Point.measurement("temperature")
                .addTag("kitid", temperature.getKitId())
                .addField("value", temperature.getValue())
                .time(Instant.from(time), WritePrecision.S);
        writeApi.writePoint(point);
    }

    public void writeHumidity(Humidity humidity) {

        Point point = com.influxdb.client.write.Point.measurement("humidity")
                .addTag("kitid", humidity.getKitId())
                .addField("value", humidity.getValue())
                .time(Instant.from(time), WritePrecision.S);
        writeApi.writePoint(point);
    }

    public void writeCdc(Illuminance illuminance) {

        Point point = com.influxdb.client.write.Point.measurement("illuminance")
                .addTag("kitid", illuminance.getKitId())
                .addField("value", illuminance.getValue())
                .time(Instant.from(time), WritePrecision.S);
        writeApi.writePoint(point);
    }

    public void writeSoil(SoilHumidity soil) {

        Point point = Point.measurement("soilHumidity")
                .addTag("kitid", soil.getKitId())
                .addField("value", soil.getValue())
                .time(Instant.from(time), WritePrecision.S);
        writeApi.writePoint(point);
    }

    public void writeData() {
        /*for (int index = 1; index <= 1; index++) {
            Point point = Point.measurement("kit1")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("farmname", "test" + 4)
                    .addField("huminity", (float)(0.12+1))
                    .addField("moisture", (float)(0.23+2))
                    .addField("temperature",(float)(0.46+3))
                    .build();
           // influxDBTemplate.write(point);
        }*/
    }

    public void writeActuator(Actuator actuator) {
        actuatorRepository.save(actuator);
    }


    public List<QueryResult.Result> selectDataSensor(String kitId, String sensor, String date) {

        String flux = "from(bucket:\"test\") |> range(start: -" + date + ")" +
                "  |> filter(fn: (r) =>\n" +
                "      r._measurement == \"" + kitId + "\")";
        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if (fluxRecord.getValue() == null) continue;
                else {
                    logger.info(fluxRecord.getTime().atZone(ZoneId.of("Asia/Seoul")) + ": " + fluxRecord.getValue() + " kitId " + fluxRecord.getValueByKey("kit_id"));
                }

            }
        }
        QueryResult queryResult = (QueryResult) queryApi.query(flux);
        logger.info(String.valueOf(queryResult.getResults()));
        return queryResult.getResults();
//       } String queryStr = String.format("SELECT * FROM %s where kit_id = '%s' and time > now() - %s", sensor,kitId,date);
//
//
//        Query query = BoundParameterQuery.QueryBuilder.newQuery(queryStr)
//                .forDatabase("smartfarm")
//                .create();
//
//        QueryResult queryResult = influxDBTemplate.query(query);
//
//        return queryResult.getResults();
    }
}