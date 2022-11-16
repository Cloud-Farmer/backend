package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.measurement.Illuminance;
import SpringBoot.Codebase.domain.repository.AlertRepository;

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
        }

        chain.process(farm, object, alertRepository);
    }
}
