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

package jodd.joy.db;

import jodd.joy.JoyProxetta;
import jodd.jtx.meta.ReadOnlyTransaction;
import jodd.jtx.meta.ReadWriteTransaction;
import jodd.jtx.meta.Transaction;
import jodd.jtx.proxy.AnnotationTxAdvice;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.MethodWithAnnotationPointcut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class TxTestCase {

	public static class BaseService {
		@ReadWriteTransaction
		public void save() {
		}
	}

	public static class UserService extends BaseService{}

	@Test
	void testJtxAnnotations() {
		JoyProxetta joyProxetta = new JoyProxetta();

		joyProxetta.addProxyAspect(new ProxyAspect(
			AnnotationTxAdvice.class,
			((ProxyPointcut)
				MethodInfo::isPublicMethod)
				.and(MethodWithAnnotationPointcut.of(Transaction.class, ReadWriteTransaction.class, ReadOnlyTransaction.class))
		));

		joyProxetta.start();

		ProxyProxetta proxyProxetta = joyProxetta.getProxetta();

		BaseService baseService = (BaseService) proxyProxetta.proxy().setTarget(BaseService.class).newInstance();

		try {
			baseService.save();
			fail();
		}
		catch (NullPointerException npe) {}


		UserService userService = (UserService) proxyProxetta.proxy().setTarget(UserService.class).newInstance();

		try {
			userService.save();
			fail();
		}
		catch (NullPointerException npe) {}

	}


}
