package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.measurement.Temperature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TempertureChain implements AlertChain {

    private AlertChain alertChain;

    @Override
    public void setNext(AlertChain nextChain) {
        alertChain = nextChain;
    }

    @Override
    public void process(Object object) {
        if (object instanceof Temperature) {
            Temperature temperature = (Temperature) object;
            if (temperature.getValue() >= 10) { // TODO : 스마트팜 관리자가 설정하는 트리거로 알람을 지정해야함
                log.info(String.format("%s 키트의 온도가 높습니다", temperature.getKitId()));
                log.info(String.format("%s kit temperture is high", temperature.getKitId()));

            }
        }
        alertChain.process(object);
    }
}
