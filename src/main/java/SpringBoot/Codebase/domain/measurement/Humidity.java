package SpringBoot.Codebase.domain.measurement;

import lombok.Data;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import javax.persistence.Column;
import java.time.Instant;

@Data
@Measurement(name = "humidity")
public class Humidity {
    @TimeColumn
    @Column(name = "time")
    private Instant time;

    @Column(name = "value")
    private String value;

    @Column(name = "kitid")
    private String kitId;
}
