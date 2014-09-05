// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

/**
 * Context of current serialized value.
 */
public class JsonValueContext {

	protected Object value;
	protected String propertyName;
	protected int index;

	public JsonValueContext(Object value) {
		this.value = value;
	}

	/**
	 * Reuses this instance for better performances.
	 */
	public void reuse(Object value) {
		this.value = value;
		this.propertyName = null;
		this.index = 0;
	}

	/**
	 * Returns current object value.
	 */
	public Object getValue() {
		return value;
	}

	// ---------------------------------------------------------------- index

	public void incrementIndex() {
		index++;
	}

	/**
	 * Returns current index.
	 */
	public int getIndex() {
		return index;
	}

	// ---------------------------------------------------------------- json object

	/**
	 * Returns current property name.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Stores current property name.
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}