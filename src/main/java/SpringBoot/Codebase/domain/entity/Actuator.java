package SpringBoot.Codebase.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Actuator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actuator_id;

    @Column(name = "kit_id")
    private Long kitId;

    private String sensor;

    private Boolean status;

    private LocalDateTime time;
}
