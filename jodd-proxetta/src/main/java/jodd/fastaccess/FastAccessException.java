package jodd.fastaccess;

import jodd.exception.UncheckedException;

public class FastAccessException extends UncheckedException {

	public FastAccessException(Throwable t) {
		super(t);
	}

}