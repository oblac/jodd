// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.csselly.CSSellyException;
import jodd.lagarto.csselly.Selector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;
import jodd.lagarto.dom.NodeListFilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Pseudo function selector.
 */
public class PseudoFunctionSelector<E> extends Selector implements NodeFilter, NodeListFilter {

	protected static final Map<String, PseudoFunction> PSEUDO_FUNCTION_MAP;

	static {
		PSEUDO_FUNCTION_MAP = new HashMap<String, PseudoFunction>(8);

		registerPseudoFunction(PseudoFunction.NTH_CHILD.class);
		registerPseudoFunction(PseudoFunction.NTH_LAST_CHILD.class);
		registerPseudoFunction(PseudoFunction.NTH_LAST_OF_TYPE.class);
		registerPseudoFunction(PseudoFunction.NTH_OF_TYPE.class);

		registerPseudoFunction(PseudoFunction.EQ.class);
		registerPseudoFunction(PseudoFunction.GT.class);
		registerPseudoFunction(PseudoFunction.LT.class);
		registerPseudoFunction(PseudoFunction.CONTAINS.class);
	}

	/**
	 * Registers pseudo function.
	 */
	public static void registerPseudoFunction(Class<? extends PseudoFunction> pseudoFunctionType) {
		PseudoFunction pseudoFunction;
		try {
			pseudoFunction = pseudoFunctionType.newInstance();
		} catch (Exception ex) {
			throw new CSSellyException(ex);
		}
		PSEUDO_FUNCTION_MAP.put(pseudoFunction.getPseudoFunctionName(), pseudoFunction);
	}

	/**
	 * Lookups pseudo function for given pseudo function name.
	 */
	public static PseudoFunction<?> lookupPseudoFunction(String pseudoFunctionName) {
		PseudoFunction pseudoFunction = PSEUDO_FUNCTION_MAP.get(pseudoFunctionName);
		if (pseudoFunction == null) {
			throw new CSSellyException("Unsupported pseudo function: " + pseudoFunctionName);
		}
		return pseudoFunction;
	}


	// ---------------------------------------------------------------- selector

	protected final PseudoFunction<E> pseudoFunction;
	protected final String expression;
	protected final E parsedExpression;

	/**
	 * Creates pseudo function selector for given function and expression.
	 */
	@SuppressWarnings("unchecked")
	public PseudoFunctionSelector(String functionName, String expression) {
		super(Type.PSEUDO_FUNCTION);
		this.pseudoFunction = (PseudoFunction<E>) lookupPseudoFunction(functionName.trim());
		this.expression = expression;
		this.parsedExpression = pseudoFunction.parseExpression(expression);
	}

	/**
	 * Returns {@link PseudoFunction pseudo function}.
	 */
	public PseudoFunction<E> getPseudoFunction() {
		return pseudoFunction;
	}

	/**
	 * Returns expression string.
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Returns parsed expression object.
	 */
	public E getParsedExpression() {
		return parsedExpression;
	}

	/**
	 * Matches nodes with this pseudo function selector.
	 */
	public boolean accept(Node node) {
		return pseudoFunction.match(node, parsedExpression);
	}

	/**
	 * Accepts node within selected results. Invoked after results are matched.
	 */
	public boolean accept(LinkedList<Node> currentResults, Node node, int index) {
		return pseudoFunction.match(currentResults, node, index, parsedExpression);
	}

}