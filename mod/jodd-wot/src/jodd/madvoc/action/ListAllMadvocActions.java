// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.result.ActionResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper action that returns sorted list of all registered action configurations,
 * action results and interceptors. It can be subclasses or used independently.
 */
public class ListAllMadvocActions {

	@In(scope = ScopeType.CONTEXT)
	protected ActionsManager actionsManager;

	@In(scope = ScopeType.CONTEXT)
	protected InterceptorsManager interceptorsManager;
	
	@In(scope = ScopeType.CONTEXT)
	protected ResultsManager resultsManager;

	@Out
	protected List<ActionConfig> actions;

	@Out
	protected List<ActionResult> results;

	@Out
	protected List<ActionInterceptor> interceptors;

	protected void collectActionInterceptors() {
		Collection<ActionInterceptor> interceptorValues = interceptorsManager.getAllActionInterceptors().values();
		interceptors = new ArrayList<ActionInterceptor>();
		interceptors.addAll(interceptorValues);
		Collections.sort(interceptors, new Comparator<ActionInterceptor>() {
			public int compare(ActionInterceptor a1, ActionInterceptor a2) {
				return a1.getClass().getSimpleName().compareTo(a2.getClass().getSimpleName());
			}
		});
	}

	protected void collectActionResults() {
		Collection<ActionResult> resultsValues = resultsManager.getAllActionResults().values();
		results = new ArrayList<ActionResult>();
		results.addAll(resultsValues);
		Collections.sort(results, new Comparator<ActionResult>() {
			public int compare(ActionResult a1, ActionResult a2) {
				return a1.getType().compareTo(a2.getType());
			}
		});
	}

	protected void collectActionConfigs() {
		Collection<ActionConfig> values = actionsManager.getAllActionConfigurations().values();
		actions = new ArrayList<ActionConfig>(values.size());
		actions.addAll(values);
		Collections.sort(actions, new Comparator<ActionConfig>() {
			public int compare(ActionConfig a1, ActionConfig a2) {
				return a1.actionPath.compareTo(a2.actionPath);
			}
		});
	}


	protected String toSystemOut() {
		System.out.println("ACTIONS");
		System.out.println("-------");
		for (ActionConfig ac : actions) {
			if (ac.isInitialized()) {
				System.out.print("[x] ");
			} else {
				System.out.print("[ ] ");
			}
			System.out.println(ac.actionPath + "  ->  " + ac.getActionString());
		}

		System.out.println("\nINTERCEPTORS");
		System.out.println("------------");
		for (ActionInterceptor ai : interceptors) {
			if (ai.isInitialized()) {
				System.out.print("[x] ");
			} else {
				System.out.print("[ ] ");
			}
			System.out.println(ai.getClass().getName());
		}

		System.out.println("\nRESULTS");
		System.out.println("-------");
		for (ActionResult ar : results) {
			if (ar.isInitialized()) {
				System.out.print("[x] ");
			} else {
				System.out.print("[ ] ");
			}
			System.out.println(ar.getType() + "  " + ar.getClass().getName());
		}
		return "none:";
	}

}
