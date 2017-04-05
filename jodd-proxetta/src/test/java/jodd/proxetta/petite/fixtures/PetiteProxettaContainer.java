package jodd.proxetta.petite.fixtures;

import jodd.petite.*;
import jodd.petite.scope.Scope;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;

public class PetiteProxettaContainer extends PetiteContainer {

    private final ProxyProxetta proxetta;

    public PetiteProxettaContainer(ProxyProxetta proxetta, PetiteConfig petiteConfig) {
        super(petiteConfig);
        this.proxetta = proxetta;
    }

//    @Override
//    public BeanDefinition registerPetiteBean(
//            Class type,
//            String name,
//            Class<? extends Scope> scopeType,
//            WiringMode wiringMode,
//            boolean define) {
//
//        if (name == null) {
//            name = PetiteUtil.resolveBeanName(type, false);
//        }
//
//        ProxyProxettaBuilder builder = proxetta.builder();
//        builder.setTarget(type);
//        type = builder.define();
//
//        return super.registerPetiteBean(type, name, scopeType, wiringMode, false);
//    }

    /**
     * Applies proxetta on bean class before bean registration.
     */
    @Override
    protected BeanDefinition createBeanDefinitionForRegistration(String name, Class type, Scope scope, WiringMode wiringMode) {
        if (proxetta != null) {
            ProxyProxettaBuilder builder = proxetta.builder();

            builder.setTarget(type);

            type = builder.define();
        }

        return super.createBeanDefinitionForRegistration(name, type, scope, wiringMode);
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return super.getBean(type);
    }
}