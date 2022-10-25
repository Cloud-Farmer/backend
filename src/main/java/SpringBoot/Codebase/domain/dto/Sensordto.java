package SpringBoot.Codebase.domain.dto;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

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
}
