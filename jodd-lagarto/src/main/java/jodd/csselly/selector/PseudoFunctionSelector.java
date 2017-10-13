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

package jodd.csselly.selector;

import jodd.csselly.CSSellyException;
import jodd.csselly.Selector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;
import jodd.lagarto.dom.NodeListFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pseudo function selector.
 */
public class PseudoFunctionSelector<E> extends Selector implements NodeFilter, NodeListFilter {

	protected static final Map<String, PseudoFunction> PSEUDO_FUNCTION_MAP;

	static {
		PSEUDO_FUNCTION_MAP = new HashMap<>(8);

		registerPseudoFunction(PseudoFunction.NTH_CHILD.class);
		registerPseudoFunction(PseudoFunction.NTH_LAST_CHILD.class);
		registerPseudoFunction(PseudoFunction.NTH_LAST_OF_TYPE.class);
		registerPseudoFunction(PseudoFunction.NTH_OF_TYPE.class);

		registerPseudoFunction(PseudoFunction.EQ.class);
		registerPseudoFunction(PseudoFunction.GT.class);
		registerPseudoFunction(PseudoFunction.LT.class);
		registerPseudoFunction(PseudoFunction.CONTAINS.class);

		registerPseudoFunction(PseudoFunction.HAS.class);
		registerPseudoFunction(PseudoFunction.NOT.class);
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
	public boolean accept(List<Node> currentResults, Node node, int index) {
		return pseudoFunction.match(currentResults, node, index, parsedExpression);
	}

}