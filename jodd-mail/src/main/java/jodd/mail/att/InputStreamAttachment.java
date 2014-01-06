// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;
import jodd.mail.MailException;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>InputStream</code> {@link EmailAttachment email attachment}.
 */
public class InputStreamAttachment extends EmailAttachment {

	protected final InputStream inputStream;
	protected final String contentType;

	public InputStreamAttachment(InputStream inputStream, String contentType, String name, String contentId) {
		super(name, contentId);
		this.inputStream = inputStream;
		this.contentType = contentType;
	}

	/**
	 * Returns <code>ByteArrayDataSource</code>.
	 */
	@Override
	public DataSource getDataSource() {
		try {
			return new ByteArrayDataSource(inputStream, contentType);
		} catch (IOException ioex) {
			throw new MailException(ioex);
		}
	}

	/**
	 * Returns content type.
	 */
	public String getContentType() {
		return contentType;
	}

}