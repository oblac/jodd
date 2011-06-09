package jodd.util;

/**
 * Simple key-value holder.
 */
public class KeyValue<K, V> {

	protected K key;
	protected V value;

	public KeyValue() {
	}

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Sets a key.
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * Returns a key.
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Returns a value.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Sets a value.
	 */
	public void setValue(V value) {
		this.value = value;
	}

	public boolean equals(Object o) {
		if (!(o instanceof KeyValue)) {
			return false;
		}
		KeyValue that = (KeyValue) o;

		Object k1 = getKey();
		Object k2 = that.getKey();

		if (k1 == k2 || (k1 != null && k1.equals(k2))) {
			Object v1 = getValue();
			Object v2 = that.getValue();
			if (v1 == v2 || (v1 != null && v1.equals(v2))) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return (key == null ? 0 : key.hashCode()) ^
				(value == null ? 0 : value.hashCode());
	}

}
