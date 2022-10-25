package SpringBoot.Codebase.domain.measurement;

import lombok.Data;
import org.influxdb.annotation.Measurement;

import javax.persistence.Column;
import java.time.Instant;

@Data
@Measurement(name = "temperature")
public class Temperature {

    @Column(name = "time")
    private Instant time;

    @Column(name = "value")
    private String value;

    @Column(name = "kit_id")
    private String kitId;
}
