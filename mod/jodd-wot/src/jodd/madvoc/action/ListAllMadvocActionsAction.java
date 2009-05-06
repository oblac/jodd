// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action;

import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.Action;
import jodd.madvoc.ScopeType;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.InterceptorsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Collection;

/**
 * Helper action that returns sorted list of all registered action configurations
 * and action results.
 */
@MadvocAction
public class ListAllMadvocActionsAction {

	@In(scope = ScopeType.CONTEXT)
	ActionsManager actionsManager;

	@In(scope = ScopeType.CONTEXT)
	InterceptorsManager interceptorsManager;
	
	@In(scope = ScopeType.CONTEXT)
	ResultsManager resultsManager;

	@Out
	List<ActionConfig> actions;

	@Out
	List<ActionResult> results;

	@Out
	List<ActionInterceptor> interceptors;

	@Action("/madvoc-listAllActions.${ext}")
	public void view() {
		Collection<ActionConfig> values = actionsManager.getAllActionConfigurations().values();
		actions = new ArrayList<ActionConfig>(values.size());
		actions.addAll(values);
		Collections.sort(actions, new Comparator<ActionConfig>() {
			public int compare(ActionConfig a1, ActionConfig a2) {
				return a1.actionPath.compareTo(a2.actionPath);
			}
		});

		Collection<ActionResult> resultsValues = resultsManager.getAllActionResults().values();
		results = new ArrayList<ActionResult>();
		results.addAll(resultsValues);
		Collections.sort(results, new Comparator<ActionResult>() {
			public int compare(ActionResult a1, ActionResult a2) {
				return a1.getType().compareTo(a2.getType());
			}
		});

		Collection<ActionInterceptor> interceptorValues = interceptorsManager.getAllActionInterceptors().values();
		interceptors = new ArrayList<ActionInterceptor>();
		interceptors.addAll(interceptorValues);
		Collections.sort(interceptors, new Comparator<ActionInterceptor>() {
			public int compare(ActionInterceptor a1, ActionInterceptor a2) {
				return a1.getClass().getSimpleName().compareTo(a2.getClass().getSimpleName());
			}
		});
	}

	@Action(value = "/madvoc-listAllActions.out")
	public String viewToSystemOut() {
		view();
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
