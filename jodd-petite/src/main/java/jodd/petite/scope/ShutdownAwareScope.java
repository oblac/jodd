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

package jodd.petite.scope;

import jodd.petite.BeanData;
import jodd.petite.def.DestroyMethodPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Base {@link Scope} class for scopes that collect
 * destroyable beans and implement shutdown routine.
 */
public abstract class ShutdownAwareScope implements Scope {

	protected List<BeanData> destroyableBeans;

	/**
	 * Returns <code>true</code> if bean is destroyable.
	 */
	protected boolean isBeanDestroyable(final BeanData beanData) {
		DestroyMethodPoint[] dmp = beanData.definition().destroyMethodPoints();
		return dmp != null && dmp.length != 0;
	}

	/**
	 * Checks if bean data is destroyable (has destroy methods) and
	 * registers it for later {@link #shutdown()}.
	 */
	protected void registerDestroyableBeans(final BeanData beanData) {
		if (!isBeanDestroyable(beanData)) {
			return;
		}
		if (destroyableBeans == null) {
			destroyableBeans = new ArrayList<>();
		}
		destroyableBeans.add(beanData);
	}

	/**
	 * Returns number of destroyable beans that have been registered.
	 */
	protected int totalRegisteredDestroyableBeans() {
		if (destroyableBeans == null) {
			return 0;
		}
		return destroyableBeans.size();
	}

	/**
	 * Removes destroyable bean from the list and calls it destroy methods.
	 * If bean is not destroyable, does nothing. Bean gets destroyed only once.
	 */
	protected void destroyBean(final BeanData beanData) {
		if (destroyableBeans == null) {
			return;
		}
		if (!isBeanDestroyable(beanData)) {
			return;
		}
		if (destroyableBeans.remove(beanData)) {
			beanData.callDestroyMethods();
		}
	}

	/**
	 * Shutdowns the scope and calls all collected destroyable beans.
	 */
	@Override
	public void shutdown() {
		if (destroyableBeans == null) {
			return;
		}

		for (final BeanData destroyableBean : destroyableBeans) {
			destroyableBean.callDestroyMethods();
		}

		destroyableBeans.clear();
	}

}