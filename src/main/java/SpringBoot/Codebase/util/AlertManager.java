package SpringBoot.Codebase.util;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.repository.AlertRepository;
import SpringBoot.Codebase.domain.repository.SmartFarmRepository;
import SpringBoot.Codebase.util.chain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlertManager {
    AlertChain c1 = new TempertureChain();
    AlertChain c2 = new IlluminanceChain();
    AlertChain c3 = new HumidiyChain();
    AlertChain c4 = new SoilHumidityChain();

    private AlertRepository alertRepository;
    private SmartFarmRepository smartFarmRepository;

    @Autowired
    public AlertManager(AlertRepository alertRepository, SmartFarmRepository smartFarmRepository) {
        this.alertRepository = alertRepository;
        this.smartFarmRepository = smartFarmRepository;
    }

    public void run(SmartFarm farm, Object object) throws RuntimeException {
        try {
            if (farm == null) {
                return;
            }

            c1.setNext(c2);
            c2.setNext(c3);
            c3.setNext(c4);
            c4.setNext(new EndChain());

            c1.process(farm, object, alertRepository);
        } catch (RuntimeException e) {
            log.info(e.getMessage());
        }
    }

}
