// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.http;

import jodd.io.http.Http;
import jodd.io.http.HttpTransfer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGet2 {

	public static void main(String[] args) throws IOException {
		HttpTransfer request = Http.createRequest("GET", "http://jodd.org?id=1");
		request.addHeader("User-Agent", "jodd");

		URL url = request.buildURL();
		System.out.println(url);
		System.out.println(request);

		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setReadTimeout(10000);
		request.send(huc);

		HttpTransfer response = Http.readResponse(huc);
		System.out.println(response);
		huc.disconnect();
	}

}
