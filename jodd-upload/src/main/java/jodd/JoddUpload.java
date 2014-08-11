// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.typeconverter.impl.FileUploadBinder;

/**
 * Jodd UPLOAD module.
 */
public class JoddUpload {

	static {
		Jodd.module();
	}

	public void bind(Object typeConverterManagerBean) {
		if (Jodd.isModuleLoaded(Jodd.BEAN)) {
			FileUploadBinder.registerTypeConverter(typeConverterManagerBean);
		}
	}

}