package SpringBoot.Codebase.domain.service;


import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.measurement.SoilHumidity;
import SpringBoot.Codebase.domain.measurement.Temperature;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SensorService {

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

    public void writeTemperature(Temperature temperature) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("temperature")
                .addTag("kitid", temperature.getKitId())
                .addField("value", temperature.getValue())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"temperature\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }
    }

    public void writeHumidity(Humidity humidity) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("humidity")
                .addTag("kitid", humidity.getKitId())
                .addField("value", humidity.getValue())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"humidity\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }
    }

    public void writeCdc(Illuminance illuminance) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = com.influxdb.client.write.Point.measurement("illuminance")
                .addTag("kitid", illuminance.getKitId())
                .addField("value", illuminance.getValue())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"illuminance\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }
    }

    public void writeSoil(SoilHumidity soil) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));

        Point point = Point.measurement("soilHumidity")
                .addTag("kitid", soil.getKitId())
                .addField("value", soil.getValue())
                .time(Instant.from(time), WritePrecision.MS);
        writeApi.writePoint(point);

        String flux = "from(bucket:\"smartfarm\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"soilHumidity\")";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if(fluxRecord.getValue() ==null) continue;
            }
        }
    }

    public List<FluxRecord> selectDataSensor(String kitId, String sensor, String date) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url,token,org,bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        ZonedDateTime time = Instant.now().atZone(ZoneId.of("Asia/Seoul"));
        String flux = String.format("from(bucket:\"smartfarm\")|> range(start: -%s, stop: now())" +
                        " |> filter(fn: (r) => r[\"_measurement\"] == \"%s\")" +
                         "|> filter(fn: (r) => r[\"kitid\"] == \"%s\")" +
                        "|> aggregateWindow(every: 1h, fn: mean, createEmpty: false)" + // 1h 단위로 묶음
                        "|> yield(name: \"mean\")",
                            date,sensor,kitId);
        List<FluxRecord> records = null;
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            records = fluxTable.getRecords();
        }
        return records;
    }
}