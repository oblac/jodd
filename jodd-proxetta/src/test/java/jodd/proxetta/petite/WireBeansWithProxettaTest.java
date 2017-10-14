// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
package jodd.proxetta.petite;

import jodd.petite.PetiteConfig;
import jodd.petite.PetiteContainer;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.petite.fixtures.Bean1;
import jodd.proxetta.petite.fixtures.Bean2;
import jodd.proxetta.petite.fixtures.ExternalBean;
import jodd.proxetta.petite.fixtures.PetiteHelper;
import jodd.proxetta.petite.fixtures.PetiteProxettaContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WireBeansWithProxettaTest {

    private PetiteContainer petiteContainer;

    @BeforeEach
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
