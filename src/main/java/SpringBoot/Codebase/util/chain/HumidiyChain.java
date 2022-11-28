package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.Alert;
import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.repository.AlertRepository;

import java.time.LocalDateTime;

public class HumidiyChain implements AlertChain {

    private AlertChain alertChain;

    @Override
    public void setNext(AlertChain nextChain) {
        alertChain = nextChain;
    }

    @Override
    public void process(SmartFarm farm, Object object, AlertRepository alertRepository) {
        if (object instanceof Humidity) {
            Humidity humidity = (Humidity) object;
            if (humidity.getValue() >= farm.getHumidityConditionValue()) {
                String messageKr = String.format("ID %s 키트의 습도가 %d 보다 높습니다", humidity.getKitId(), farm.getHumidityConditionValue());
                String messageEng = String.format("The humidity of %s kit is higher than %d", humidity.getKitId(), farm.getHumidityConditionValue());

                Alert alert = Alert.builder()
                        .messageKR(messageKr)
                        .messageENG(messageEng)
                        .status("warning")
                        .subject("humidity")
                        .smartFarm(farm)
                        .alertedTime(LocalDateTime.now())
                        .build();
                alertRepository.save(alert);
            }

        }
        alertChain.process(farm, object, alertRepository);
    }
}
