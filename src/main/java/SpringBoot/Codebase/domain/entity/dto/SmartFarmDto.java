package SpringBoot.Codebase.domain.entity.dto;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SmartFarmDto {
    private Long id;

    private String temperatureCondition;

    private Integer temperatureConditionValue;

    private String soilHumidityCondition;
    private Integer soilHumidityConditionValue;

    private String illuminanceCondition;
    private Integer illuminanceConditionValue;

    private String humidityCondition;

    private Integer humidityConditionValue;


    private LocalDateTime createdTime;

    public static SmartFarmDto of(SmartFarm smartFarm) {
        return SmartFarmDto.builder()
                .id(smartFarm.getId())
                .temperatureCondition((smartFarm.getTemperatureCondition()))
                .temperatureConditionValue(smartFarm.getTemperatureConditionValue())
                .soilHumidityCondition(smartFarm.getSoilHumidityCondition())
                .soilHumidityConditionValue(smartFarm.getSoilHumidityConditionValue())
                .illuminanceCondition(smartFarm.getIlluminanceCondition())
                .illuminanceConditionValue(smartFarm.getIlluminanceConditionValue())
                .humidityCondition(smartFarm.getHumidityCondition())
                .humidityConditionValue(smartFarm.getHumidityConditionValue())
                .createdTime(smartFarm.getCreatedTime())
                .build();

    }

}
