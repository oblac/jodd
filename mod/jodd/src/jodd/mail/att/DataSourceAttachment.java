// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;

/**
 * Generic data source adapter for attachments.
 */
public class DataSourceAttachment extends EmailAttachment {

	protected final DataSource dataSource;

	public DataSourceAttachment(DataSource dataSource, String name, String contentId) {
		super(name, contentId);
		this.dataSource = dataSource;
	}

	public DataSourceAttachment(DataSource dataSource, String name) {
		super(name, null);
		this.dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}
