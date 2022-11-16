package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.measurement.Humidity;

public class HumidiyChain implements AlertChain {

    private AlertChain alertChain;

    @Override
    public void setNext(AlertChain nextChain) {
        alertChain = nextChain;
    }

    @Override
    public void process(Object object) {
        if (object instanceof Humidity) {
            Humidity humidity = (Humidity) object;

        }
        alertChain.process(object);
    }
}
