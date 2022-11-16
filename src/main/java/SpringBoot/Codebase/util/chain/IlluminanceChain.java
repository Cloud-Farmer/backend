package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.Alert;
import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.repository.AlertRepository;

import java.time.LocalDateTime;

public class  IlluminanceChain implements AlertChain {

    private AlertChain chain;

    @Override
    public void setNext(AlertChain nextChain) {
        chain = nextChain;
    }

    @Override
    public void process(SmartFarm farm, Object object, AlertRepository alertRepository) {
        if (object instanceof Illuminance) {
            Illuminance illuminance = (Illuminance) object;
            if (illuminance.getValue() >= farm.getIlluminanceConditionValue()) {
                String message = String.format("%s 키트의 조도 %d 보다 높습니다", illuminance.getKitId(), farm.getIlluminanceConditionValue());

                Alert alert = Alert.builder()
                        .language("kr")
                        .message(message)
                        .status("warning")
                        .subject("illuminance")
                        .smartFarm(farm)
                        .alertedTime(LocalDateTime.now())
                        .build();
                alertRepository.save(alert);
            }

        }

        chain.process(farm, object, alertRepository);
    }
}
