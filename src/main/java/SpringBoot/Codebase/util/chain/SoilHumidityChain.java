package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.Alert;
import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.measurement.SoilHumidity;
import SpringBoot.Codebase.domain.repository.AlertRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class SoilHumidityChain implements AlertChain {

    private AlertChain alertChain;

    @Override
    public void setNext(AlertChain nextChain) {
        alertChain = nextChain;
    }

    @Override
    public void process(SmartFarm farm, Object object, AlertRepository alertRepository) {
        if (object instanceof SoilHumidity) {
            SoilHumidity soilHumidity = (SoilHumidity) object;
            if (soilHumidity.getValue() >= farm.getHumidityConditionValue()) {
                String messageKr = String.format("ID %s 키트의 토양습도가 %d 보다 높습니다", soilHumidity.getKitId(), farm.getSoilHumidityConditionValue());
                String messageEng = String.format("The soilhumidity of %s kit is higher than %d", soilHumidity.getKitId(), farm.getSoilHumidityConditionValue());

                Alert alert = Alert.builder()
                        .messageKR(messageKr)
                        .messageENG(messageEng)
                        .status("warning")
                        .subject("soilhumidity")
                        .smartFarm(farm)
                        .alertedTime(LocalDateTime.now())
                        .build();
                alertRepository.save(alert);
            }
        }
        alertChain.process(farm, object, alertRepository);
    }
}
