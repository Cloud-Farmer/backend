package SpringBoot.Codebase.domain.actuator;

import lombok.Data;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import javax.persistence.Column;
import java.time.Instant;

@Data
@Measurement(name = "window")
public class Window implements Actuator{

    @TimeColumn
    @Column(name = "time")
    private Instant time;

    @Column(name="status")
    private Float status;

    @Column(name = "kitid")
    private String kitId;
}
