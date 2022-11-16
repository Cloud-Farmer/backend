package SpringBoot.Codebase.util;

import SpringBoot.Codebase.util.chain.*;
import org.springframework.stereotype.Component;

@Component
public class AlertManager {
    AlertChain c1 = new TempertureChain();
    AlertChain c2 = new IlluminanceChain();
    AlertChain c3 = new HumidiyChain();
    AlertChain c4 = new SoilHumidityChain();

    public void run(Object object) throws RuntimeException {
        c1.setNext(c2);
        c2.setNext(c3);
        c3.setNext(c4);
        c4.setNext(new EndChain());

        c1.process(object);
    }
}
