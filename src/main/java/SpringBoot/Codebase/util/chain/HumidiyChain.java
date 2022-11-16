package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.measurement.Humidity;
import SpringBoot.Codebase.domain.repository.AlertRepository;

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

        }
        alertChain.process(farm, object, alertRepository);
    }
}
