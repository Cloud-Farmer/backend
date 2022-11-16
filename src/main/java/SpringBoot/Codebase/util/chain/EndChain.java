package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.repository.AlertRepository;

public class EndChain implements AlertChain {
    private AlertChain end;
    @Override
    public void setNext(AlertChain nextChain) {
        end = null;
    }

    @Override
    public void process(SmartFarm farm, Object object, AlertRepository alertRepository) {

    }
}
