// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.filter.DefaultWebAppFilters;

import java.util.Set;

/**
 * Manager for Madvoc filters.
 * @see jodd.madvoc.component.InterceptorsManager
 */
public class FiltersManager extends WrapperManager<ActionFilter> {

	@Override
	protected ActionFilter[] createArray(int len) {
		return new ActionFilter[len];
	}

	@Override
	protected Class<? extends ActionFilter> getDefaultWebAppWrapper() {
		return DefaultWebAppFilters.class;
	}

	@Override
	protected Class<? extends ActionFilter>[] getDefaultWrappers() {
		return madvocConfig.getDefaultFilters();
	}

	/**
	 * Returns all filters.
	 */
	public Set<ActionFilter> getAllFilters() {
		return super.getAll();
	}

}