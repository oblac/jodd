// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import java.io.Serializable;

public abstract class WAbstract<T, E extends Serializable> {

	private Integer field;

	private E eee;

	private T ttt;

	public E getEee() {
		return eee;
	}

	public void setEee(E eee) {
		this.eee = eee;
	}

	public Integer getField() {
		return field;
	}

	public void setField(Integer field) {
		this.field = field;
	}

	public T getTtt() {
		return ttt;
	}

	public void setTtt(T ttt) {
		this.ttt = ttt;
	}


	public abstract void setEkola(E eee);
	public abstract void setTrtica(T ttt);
}
