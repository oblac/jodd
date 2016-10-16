package jodd.petite.prox;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

public class LogProxyAdvice implements ProxyAdvice {

    @Override
    public Object execute() throws Exception {
        System.out.println("execute now : " + ProxyTarget.targetClass().getCanonicalName());
        return ProxyTarget.invoke();
    }
}
