package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.measurement.Illuminance;

public class  IlluminanceChain implements AlertChain {

    private AlertChain chain;

    @Override
    public void setNext(AlertChain nextChain) {
        chain = nextChain;
    }

    @Override
    public void process(Object object) {
        if (object instanceof Illuminance) {
            Illuminance illuminance = (Illuminance) object;
        }

        chain.process(object);
    }
}
