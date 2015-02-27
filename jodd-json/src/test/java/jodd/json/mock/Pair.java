// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.mock;

public class Pair<T, U> {
	private T first;
	private U second;

	protected Pair() {
	}

	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	protected void setFirst(T first) {
		this.first = first;
	}

	public U getSecond() {
		return second;
	}

	protected void setSecond(U second) {
		this.second = second;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pair pair = (Pair) o;

		if (first != null ? !first.equals(pair.first) : pair.first != null) {
			return false;
		}
		if (second != null ? !second.equals(pair.second) : pair.second != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result = (first != null ? first.hashCode() : 0);
		result = 31 * result + (second != null ? second.hashCode() : 0);
		return result;
	}
}
