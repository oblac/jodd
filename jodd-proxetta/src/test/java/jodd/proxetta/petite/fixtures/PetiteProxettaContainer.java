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