// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.debug;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;
import jodd.typeconverter.Convert;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Loggable advice over <code>java.sql.PreparedStatement</code>.
 */
public class LoggableAdvice implements ProxyAdvice {

	/**
	 * Used for storing parameter values needed for producing log.
	 */
	protected ArrayList<String> parameterValues;

	public String sqlTemplate;

	public Object execute() {
		int position = ((Integer) ProxyTarget.argument(1)).intValue();

		if (ProxyTarget.targetMethodName().equals("setNull")) {
			saveQueryParamValue(position, null);
		} else {
		saveQueryParamValue(position, ProxyTarget.argument(2));
		}
		return ProxyTarget.invoke();
	}

	// ---------------------------------------------------------------- additional methods

	/**
	 * Returns the query string.
	 */
	public String getQueryString() {
		if (sqlTemplate == null) {
			return toString();
		}
		if (parameterValues == null) {
			return sqlTemplate;
		}

		StringBuilder sb = new StringBuilder();

		int qMarkCount = 0;
		StringTokenizer tok = new StringTokenizer(sqlTemplate + ' ', "?");
		while (tok.hasMoreTokens()) {
			String oneChunk = tok.nextToken();
			sb.append(oneChunk);
			try {
				Object value;
				if (parameterValues.size() > 1 + qMarkCount) {
					value = parameterValues.get(1 + qMarkCount);
					qMarkCount++;
				} else {
					if (tok.hasMoreTokens()) {
						value = null;
					} else {
						value = "";
					}
				}
				sb.append(value);
			} catch (Throwable th) {
				sb.append("--- Building query failed: ").append(th.toString());
			}
		}
		return sb.toString().trim();
	}

	/**
	 * Saves the parameter value <code>obj</code> for the specified <code>position</code>
	 * for use in logging output.
	 *
	 * @param position position (starting at 1) of the parameter to save
	 * @param obj java.lang.Object the parameter value to save
	 */
	private void saveQueryParamValue(int position, Object obj) {
		String strValue;
		if (obj instanceof String || obj instanceof Date) {
			strValue = "'" + obj + '\'';        // if we have a String or Date , include '' in the saved value
		}
		else if (obj == null) {
			strValue = "<null>";				// convert null to the string null
		}
		else {
			strValue = Convert.toString(obj);	// all other objects (includes all Numbers, arrays, etc)
		}

		// if we are setting a position larger than current size of parameterValues, first make it larger
		if (parameterValues == null) {
			parameterValues = new ArrayList<String>();
		}
		while (position >= parameterValues.size()) {
			parameterValues.add(null);
		}
		parameterValues.set(position, strValue);
	}

}
