package SpringBoot.Codebase.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "smart_farm")
@NoArgsConstructor
@AllArgsConstructor
public class SmartFarm { // 회원 FK, 농장주

    @Id
    @Column(name = "smartfarm_id")
    private Long id;

    private String temperatureCondition;
    private Integer temperatureConditionValue;

    private String soilHumidityCondition;
    private Integer soilHumidityConditionValue;

    private String illuminanceCondition;
    private Integer illuminanceConditionValue;

    private String humidityCondition;
    private Integer humidityConditionValue;

    @Column(nullable = false)
    private String mqttAdapterId;

    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "smartFarm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alert> alerts = new ArrayList<>();
}
