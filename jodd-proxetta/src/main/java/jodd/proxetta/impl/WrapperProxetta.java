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

package jodd.proxetta.impl;

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxettaNames;
import jodd.proxetta.ProxyAspect;

/**
 * Proxetta that creates wrappers.
 */
public class WrapperProxetta extends Proxetta<WrapperProxetta, ProxyAspect> {

	protected boolean createTargetInDefaultCtor;

	public WrapperProxetta() {
		classNameSuffix = ProxettaNames.wrapperClassNameSuffix;
	}

	@Override
	public WrapperProxettaFactory proxy() {
		return new WrapperProxettaFactory(this);
	}

	/**
	 * Defines if target should be created in ctor, so no additional injection is required.
	 */
	public WrapperProxetta setCreateTargetInDefaultCtor(final boolean createTargetInstanceInDefaultCtor) {
		this.createTargetInDefaultCtor = createTargetInstanceInDefaultCtor;
		return _this();
	}


}
