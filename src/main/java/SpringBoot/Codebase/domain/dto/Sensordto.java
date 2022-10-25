package SpringBoot.Codebase.domain.dto;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.springframework.beans.factory.annotation.Value;

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
