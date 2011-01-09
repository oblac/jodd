// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.db;

import jodd.db.DbQuery;
import jodd.db.pool.CoreConnectionPool;
import jodd.util.ThreadUtil;

import java.sql.Connection;

public class ConnectionPoolExample {

	public static void main(String[] args) {
		CoreConnectionPool cp = new CoreConnectionPool();
		cp.setDriver("com.mysql.jdbc.Driver");
		cp.setUrl("jdbc:mysql://localhost:3306/uphea?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&autoReconnect=true");
		cp.setUser("root");
		cp.setPassword("root!");
		cp.setMaxConnections(10);
		cp.setMinConnections(3);
		cp.setValidateConnection(true);
		cp.setValidationTimeout(10000);
		cp.setDefaultValidationQuery();
		cp.init();

		pingdb(cp);
		System.out.println("wait 110!");
		ThreadUtil.sleep(110L * 1000);

		pingdb(cp);

		cp.close();
	}


	private static void pingdb(CoreConnectionPool cp) {
		Connection c = cp.getConnection();
		DbQuery q = new DbQuery(c, "select count(1) from up_user;");
		long i = q.executeCountAndClose();
		System.out.println(c);
		cp.closeConnection(c);
		System.out.println("i = " + i);
	}


}
