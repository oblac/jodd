// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;

/**
 * File {@link EmailAttachment email attachment}.
 * Content type is not set by user, but by <code>javax.mail</code>
 * framework.
 */
public class FileAttachment extends EmailAttachment {

	protected final File file;

	public FileAttachment(File file, String name, String contentId) {
		super(name, contentId);
		this.file = file;
	}

	/**
	 * Returns attached file.
	 */
	public File getFile() {
		return file;
	}

	@Override
	public DataSource getDataSource() {
		return new FileDataSource(file);
	}
}
