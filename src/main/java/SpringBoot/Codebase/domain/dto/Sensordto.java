package SpringBoot.Codebase.domain.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;

@Measurement(name = "smartfarm_db")
public class Sensordto {

    @TimeColumn
    @Column(name = "humidity")
    private Double humidity;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "cdc")
    private Double cdc;
    @Column(name = "soil")
    private Double soil;
    @Column(name = "farmname")
    private String farmName;
}