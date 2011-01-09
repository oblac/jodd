// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;

/**
 * File attachment.
 */
public class FileAttachment extends EmailAttachment {

	protected final File file;

	public FileAttachment(File file, String name, String contentId) {
		super(name, contentId);
		this.file = file;
	}
	public FileAttachment(File file, boolean inline) {
		super(file.getName(), inline ? file.getName() : null);
		this.file = file;
	}
	public FileAttachment(File file) {
		this(file, false);
	}

	public File getFile() {
		return file;
	}

	@Override
	public DataSource getDataSource() {
		return new FileDataSource(file);
	}
}
