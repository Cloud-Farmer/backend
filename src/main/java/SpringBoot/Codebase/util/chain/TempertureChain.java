package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.Alert;
import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.measurement.Temperature;
import SpringBoot.Codebase.domain.repository.AlertRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class TempertureChain implements AlertChain {

    private AlertChain alertChain;

    @Override
    public void setNext(AlertChain nextChain) {
        alertChain = nextChain;
    }

    @Override
    public void process(SmartFarm farm, Object object, AlertRepository alertRepository) {
        if (object instanceof Temperature) {
            Temperature temperature = (Temperature) object;
            if (temperature.getValue() >= farm.getTemperatureConditionValue()) { // TODO : 스마트팜 관리자가 설정하는 트리거로 알람을 지정해야함
                log.info(String.format("%s 키트의 온도가 %d 보다 높습니다", temperature.getKitId(), farm.getTemperatureConditionValue()));

                Alert alert = Alert.builder()
                        .language("kr")
                        .message(String.format("%s 키트의 온도가 %d 보다 높습니다", temperature.getKitId(), farm.getTemperatureConditionValue()))
                        .status("warning")
                        .subject("temperature")
                        .smartFarm(farm)
                        .alertedTime(LocalDateTime.now())
                        .build();
                alertRepository.save(alert);
            }
        }
        alertChain.process(farm,object, alertRepository);
    }
}
