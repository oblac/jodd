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

package jodd.proxetta.fixtures.data;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import static jodd.proxetta.ProxyTarget.targetClassAnnotation;

public class HeroProxyAdvice2 implements ProxyAdvice {

	@Override
	public Object execute() throws Exception {
		String name = "";

		String heroName = (String) targetClassAnnotation(HeroName.class.getName(), "value");

		name += heroName;

		Class heroClass = (Class) targetClassAnnotation(HeroName.class.getName(), "power");

		name += heroClass.getSimpleName();

		Integer secret = (Integer) targetClassAnnotation(HeroName.class.getName(), "secret");

		name += secret;

		Character middle = (Character) targetClassAnnotation(HeroName.class.getName(), "middle");

		name += middle;

		Double opacity = (Double) targetClassAnnotation(HeroName.class.getName(), "opacity");

		name += opacity;

		String[] helpers = (String[]) targetClassAnnotation(HeroName.class.getName(), "helpers");

		name += helpers[0];

		int[] enemies = (int[]) targetClassAnnotation(HeroName.class.getName(), "enemies");

		name += enemies[1];

		Hero.POWER power = (Hero.POWER) targetClassAnnotation(HeroName.class.getName(), "power2");

		name += power;

		Hero.POWER[] subpowers = (Hero.POWER[]) targetClassAnnotation(HeroName.class.getName(), "subpowers");

		name += subpowers[0];

		String missing = (String)  targetClassAnnotation(HeroName.class.getName(), "xxxxx");

		name += missing;

		return ProxyTarget.returnValue(name);
	}
}