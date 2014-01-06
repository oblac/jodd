// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EchoTestServer extends NanoHTTPD {

	public EchoTestServer() throws IOException {
		super(8081, new File("."));
	}

	public String uri;

	public String method;

	public Properties header;

	public Properties params;

	public Properties files;

	public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
		String msg = method + " " + uri;

		this.uri = uri;
		this.method = method;
		this.header = header;
		this.params = parms;
		this.files = files;

		return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, msg);
	}
}