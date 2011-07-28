// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;
import java.io.InputStream;

/**
 * Input stream attachment.
 */
public class InputStreamAttachment extends EmailAttachment {

	protected final InputStream inputStream;
	protected final String contentType;

	public InputStreamAttachment(InputStream inputStream, String contentType, String name, String contentId) {
		super(name, contentId);
		this.inputStream = inputStream;
		this.contentType = contentType;
	}

	@Override
	public DataSource getDataSource() {
		return new StreamDataSource(inputStream, contentType);
	}

}
