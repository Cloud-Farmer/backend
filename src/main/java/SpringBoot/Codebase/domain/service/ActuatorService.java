package SpringBoot.Codebase.domain.service;

import SpringBoot.Codebase.config.MqttConfiguration;
import SpringBoot.Codebase.domain.actuator.Fan;
import SpringBoot.Codebase.domain.actuator.Led;
import SpringBoot.Codebase.domain.actuator.Pump;
import SpringBoot.Codebase.domain.actuator.Window;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
@Service
public class ActuatorService {
    private MqttConfiguration.MqttOrderGateway mqttOrderGateway;

    @Value("${spring.influxdb2.token}")
    private char [] token;

    @Value("${spring.influxdb2.org}")
    private String org;

    @Value("${spring.influxdb2.bucket}")
    private String bucket;

    @Value("${spring.influxdb2.url}")
    private String url;

    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;
    private ZonedDateTime time;
    @Autowired
    public ActuatorService(MqttConfiguration.MqttOrderGateway mqttOrderGateway){
        this.mqttOrderGateway = mqttOrderGateway;
    }
    public void sentToMqtt(String kitId, String sensor, String available) {
        String topic = kitId + "/actuator/" + sensor;
        mqttOrderGateway.sendToMqtt(available, topic);

    }
    public void writeFan(Fan fan){

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("fan")
                .addTag("kitid", fan.getKitId())
                .addField("status", fan.getStatus())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"fan\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }

    }
    public void writeLed(Led led){

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("led")
                .addTag("kitid", led.getKitId())
                .addField("status", led.getStatus())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"led\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }

    }
    public void writeWindow(Window window){

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("window")
                .addTag("kitid", window.getKitId())
                .addField("status", window.getStatus())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"window\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }

    }
    public void writePump(Pump pump){

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("pump")
                .addTag("kitid", pump.getKitId())
                .addField("status", pump.getStatus())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"pump\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }

    }
    public List<FluxRecord> selectActuator(String kitId, String actuator,String date) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));
        String flux = String.format("from(bucket:\"smartfarm\")|> range(start: -%s, stop: now())" +
                        " |> filter(fn: (r) => r[\"_measurement\"] == \"%s\")" +
                        "|> filter(fn: (r) => r[\"kitid\"] == \"%s\")" +
                        "|> aggregateWindow(every: 1h, fn: mean, createEmpty: false)" + // 1h 단위로 묶음
                        "|> yield(name: \"mean\")",
                date,actuator,kitId);
        List<FluxRecord> records = null;
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            records = fluxTable.getRecords();
        }
        return records;
    }
}
