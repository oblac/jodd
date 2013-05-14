package jodd.http;

import org.apache.catalina.startup.Tomcat;


/**
 * Madvoc Tomcat Server.
 */
public class TomcatServer extends TestServer {

	protected Tomcat tomcat;

	public void start() throws Exception {
		super.start();

		String workingDir = System.getProperty("java.io.tmpdir");

		tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.setBaseDir(workingDir);
		tomcat.addWebapp("/", webRoot.getAbsolutePath());

		tomcat.start();
	}

	public void stop() throws Exception {
		tomcat.stop();
		tomcat.destroy();
		super.stop();
	}
}