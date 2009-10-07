// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.madvoc.injector.ContextInjector;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;
import jodd.petite.PetiteContainer;

/**
 * Injectors manager component for all injectors.
 * It stores default implementations of all injectors so they can be configured from the outside.
 */
public class InjectorsManager {

	@PetiteInject
	protected ScopeDataManager scopeDataManager;

	@PetiteInject
	protected PetiteContainer madpc;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ContextInjector contextInjector;


	/**
	 * Creates all injectors, after class creation.
	 */
	@PetiteInitMethod(order = 1, firstOff = true)
	void createInjectors() {
		requestScopeInjector = createRequestScopeInjector();
		sessionScopeInjector = createSessionScopeInjector();
		contextInjector = createContextInjector();
	}

	/**
	 * Returns scope data manager used for creating injectors.
	 */
	public ScopeDataManager getScopeDataManager() {
		return scopeDataManager;
	}


	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link jodd.madvoc.injector.RequestScopeInjector}.
	 */
	public RequestScopeInjector getRequestScopeInjector() {
		return requestScopeInjector;
	}

	/**
	 * Returns {@link jodd.madvoc.injector.SessionScopeInjector}
	 */
	public SessionScopeInjector getSessionScopeInjector() {
		return sessionScopeInjector;
	}

	/**
	 * Returns {@link jodd.madvoc.injector.ContextInjector}
	 */
	public ContextInjector getContextInjector() {
		return contextInjector;
	}


	// ---------------------------------------------------------------- new

	/**
	 * Creates new request scope injector.
	 */
	public RequestScopeInjector createRequestScopeInjector() {
		return new RequestScopeInjector(scopeDataManager);
	}

	/**
	 * Creates new session scope injector.
	 */
	public SessionScopeInjector createSessionScopeInjector() {
		return new SessionScopeInjector(scopeDataManager);
	}

	/**
	 * Creates new contextinjector.
	 */
	public ContextInjector createContextInjector() {
		return new ContextInjector(scopeDataManager, madpc);
	}

}
