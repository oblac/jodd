package jodd.petite.prox;

import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import jodd.petite.prox.example1.ExternalBean;
import jodd.petite.proxetta.ProxettaAwarePetiteContainer;
import jodd.proxetta.impl.ProxyProxetta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class MixedScope343Test {

	private PetiteContainer petiteContainer;

	@Before
	public void setupPetiteContainer() {
		PetiteConfig petiteConfig = PetiteHelper.createPetiteConfig();

		ProxyProxetta proxyProxetta = PetiteHelper.createProxyProxetta();
		petiteContainer = new ProxettaAwarePetiteContainer(proxyProxetta, petiteConfig);

		AutomagicPetiteConfigurator petiteConfigurator = new AutomagicPetiteConfigurator();
		petiteConfigurator.setIncludedEntries(this.getClass().getPackage().getName() + ".*");
		petiteConfigurator.configure(petiteContainer);
	}

	@After
	public void teardownPetiteContainer() {
		petiteContainer.shutdown();
	}

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	public void cleanUpStreams() {
		System.setOut(null);
		System.setErr(null);
	}

	@Test
	public void testWithMixingScopesSingletonAndProto(){
		ExternalBean externalBean = new ExternalBean();
		// --> inject

		petiteContainer.wire(externalBean);

		// <-- injection done

		System.out.println("RUN!");
		externalBean.execute();

		assertEquals("RUN!\n" +
			"execute now : jodd.petite.prox.example1.impl.MainPetiteBean\n" +
			"execute now : jodd.petite.prox.example1.impl.SubPetiteBean\n" +
			"Executing jodd.petite.prox.example1.impl.SubPetiteBean$$Proxetta\n" +
			"executing jodd.petite.prox.example1.impl.MainPetiteBean$$Proxetta\n" +
			"executing non jodd petite bean -> jodd.petite.prox.example1.ExternalBean\n",
			outContent.toString());
	}
}
