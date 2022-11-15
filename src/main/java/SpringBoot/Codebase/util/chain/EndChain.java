package SpringBoot.Codebase.util.chain;

public class EndChain implements AlertChain {
    private AlertChain end;
    @Override
    public void setNext(AlertChain nextChain) {
        end = null;
    }

    @Override
    public void process(Object object) {

    }
}
