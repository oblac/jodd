// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mail.att;

import jodd.mail.EmailAttachment;

import javax.activation.DataSource;

/**
 * Generic <code>DataSource</code> adapter for attachments.
 */
public class DataSourceAttachment extends EmailAttachment {

	protected final DataSource dataSource;

	public DataSourceAttachment(DataSource dataSource, String name, String contentId) {
		super(name, contentId);
		this.dataSource = dataSource;
	}

	/**
	 * Returns wrapped data source.
	 */
	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}
