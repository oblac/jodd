// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.data;

import java.io.Serializable;

public interface WInterface<T extends Serializable, D> {

	void setDada(D d);
	void setTata(T t);
}
