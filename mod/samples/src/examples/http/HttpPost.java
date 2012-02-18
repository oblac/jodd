// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.http;

import jodd.io.http.Http;
import jodd.io.http.HttpParams;
import jodd.io.http.HttpTransfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpPost {

	public static void main(String[] args) throws IOException {
		HttpTransfer request = Http.createRequest("POST", "http://127.0.0.1:8080/upload.exec.html");

		HttpParams params = new HttpParams();
		params.addParameter("boo", "Text");
		params.addParameter("foo.file1", new File("d:\\a.jpg"));
		request.setRequestParameters(params);

		System.out.println(request);

		Socket socket = new Socket(request.getHost(), request.getPort());
		OutputStream out = socket.getOutputStream();
		request.send(out);

		InputStream in = socket.getInputStream();
		HttpTransfer response = Http.readResponse(in);
		System.out.println(response.toString());
		socket.close();

	}
}
