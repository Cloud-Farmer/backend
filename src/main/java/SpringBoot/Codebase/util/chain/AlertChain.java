package SpringBoot.Codebase.util.chain;

import lombok.extern.slf4j.Slf4j;
public interface AlertChain {
    public void setNext(AlertChain nextChain);
    public void process(Object object);

}
