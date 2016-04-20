package jodd.proxetta.petite;

import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.petite.data.Bean1;
import jodd.proxetta.petite.data.Bean2;
import jodd.proxetta.petite.data.ExternalBean;
import jodd.proxetta.petite.data.PetiteHelper;
import jodd.proxetta.petite.data.PetiteProxettaContainer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WireBeansWithProxettaTest {

    private PetiteContainer petiteContainer;

    @Before
    public void setupPetiteContainer() {
        PetiteConfig petiteConfig = PetiteHelper.createPetiteConfig();

        ProxyProxetta proxyProxetta = PetiteHelper.createProxyProxetta();
        petiteContainer = new PetiteProxettaContainer(proxyProxetta, petiteConfig);

        //AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
        //petiteConfigurator.configure(petiteContainer);

        petiteContainer.registerPetiteBean(Bean1.class);
        petiteContainer.registerPetiteBean(Bean2.class);
    }

    public void teardownPetiteContainer() {
        petiteContainer.shutdown();
    }

    @Test
    public void testWireExternalBeanAndCheckInjectedBean2Reference(){
        ExternalBean externalBean = new ExternalBean();

        // --> inject
        petiteContainer.wire(externalBean);
        // <-- injection done

        Object value = externalBean.execute();

        assertNotNull(value);

        assertTrue(value instanceof Bean2);
    }

}
