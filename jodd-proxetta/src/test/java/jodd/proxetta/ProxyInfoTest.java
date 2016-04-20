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

package jodd.proxetta;

import jodd.datetime.JDateTime;
import jodd.proxetta.data.DateDao;
import jodd.proxetta.data.PerformanceMeasureProxyAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllTopMethodsPointcut;
import jodd.util.SystemUtil;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class ProxyInfoTest {

	@Test
	public void testProxyInfo_createNotRightAfterTheMethod() {
		ProxyProxetta proxetta = ProxyProxetta.withAspects(aspects());
		proxetta.setDebugFolder(SystemUtil.userHome());

		DateDao dateDateProxy = (DateDao) proxetta.builder(DateDao.class).newInstance();

		JDateTime jDateTime = dateDateProxy.currentTime();

		assertNotNull(jDateTime);
	}

	private ProxyAspect[] aspects() {
		ProxyAspect aspect_performance = new ProxyAspect(
			PerformanceMeasureProxyAdvice.class, new AllTopMethodsPointcut());

		return new ProxyAspect[] {aspect_performance};
	}

}