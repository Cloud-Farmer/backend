package SpringBoot.Codebase.util.chain;

public class SoilHumidityChain implements AlertChain {

    private AlertChain alertChain;

    @Override
    public void setNext(AlertChain nextChain) {
        alertChain = nextChain;
    }

    @Override
    public void process(Object object) {
        if (object instanceof SoilHumidityChain) {
            SoilHumidityChain soilHumidity = (SoilHumidityChain) object;
            //
        }
        alertChain.process(object);
    }
}
