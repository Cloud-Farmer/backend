package SpringBoot.Codebase.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
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

    private String mqttAdapterId;
    private String beanId;
}
