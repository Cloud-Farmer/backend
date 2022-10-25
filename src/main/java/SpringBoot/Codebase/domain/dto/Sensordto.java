package SpringBoot.Codebase.domain.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

<<<<<<< HEAD
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

=======
import java.time.Instant;

@Data
@Measurement(name = "sensor")
public class Sensordto {

    @TimeColumn
    @Column(name = "time")
    private Instant time;

    @Column(name = "value")
    private String value;

    @Column(name = "kitid")
    private String kitId;
>>>>>>> 9fcb0b52fb08da2b9a220ee559158639cd9cccd3
}
