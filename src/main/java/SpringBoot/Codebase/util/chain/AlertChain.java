package SpringBoot.Codebase.util.chain;

import SpringBoot.Codebase.domain.entity.SmartFarm;
import SpringBoot.Codebase.domain.repository.AlertRepository;

public interface AlertChain {
    public void setNext(AlertChain nextChain);
    public void process(SmartFarm farm, Object object, AlertRepository alertRepository);

}
