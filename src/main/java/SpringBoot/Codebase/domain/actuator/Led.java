package SpringBoot.Codebase.domain.actuator;

import lombok.Data;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import javax.persistence.Column;
import java.time.Instant;

@Data
@Measurement(name = "led")
public class Led implements Actuator {

    @TimeColumn
    @Column(name = "time")
    private Instant time;

    @Column(name="status")
    private Float status;

    @Column(name = "kitid")
    private String kitId;

}
