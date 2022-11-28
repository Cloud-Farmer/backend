package SpringBoot.Codebase.domain.measurement;

import lombok.Data;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;

import javax.persistence.Column;
import java.time.Instant;

@Data
@Measurement(name = "soilHumidity")
public class SoilHumidity {
        @TimeColumn
        @Column(name = "time")
        private Instant time;

        @Column(name = "value")
        private Float value;

        @Column(name = "kitid")
        private String kitId;

}
