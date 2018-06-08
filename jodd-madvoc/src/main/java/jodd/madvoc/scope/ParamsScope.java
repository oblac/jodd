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

package jodd.madvoc.scope;

import jodd.madvoc.ActionRequest;
import jodd.madvoc.config.Targets;
import jodd.petite.ParamManager;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;

import javax.servlet.ServletContext;

/**
 * Special kind of scope that is used on all targets.
 */
public class ParamsScope implements MadvocScope {

	@PetiteInject
	PetiteContainer madpc;

	@Override
	public void inject(final ActionRequest actionRequest, final Targets targets) {

	}

	@Override
	public void inject(final ServletContext servletContext, final Targets targets) {

	}

	@Override
	public void inject(final Targets targets) {
		targets.forEachTarget(target -> {
			final Class targetType = target.resolveType();
			final String baseName = baseNameOf(targetType);

			final ParamManager madvocPetiteParamManager = madpc.paramManager();

			final String[] params = madvocPetiteParamManager.filterParametersForBeanName(baseName, true);

			for (final String param : params) {
				final Object value = madvocPetiteParamManager.get(param);

				final String propertyName = param.substring(baseName.length() + 1);

				target.writeValue(propertyName, value, false);
			}
		});
	}

	/**
	 * Returns base name of the web component for the properties.
	 */
	protected String baseNameOf(final Class targetType) {
		return madpc.resolveBeanName(targetType);
	}

	@Override
	public void outject(final ActionRequest actionRequest, final Targets targets) {

	}
}
