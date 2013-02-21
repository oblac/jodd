package jodd.http;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

class Liferay {

	private static void sendGet1() {
		HttpResponse response = HttpRequest
				.get("http://192.168.1.111:8080/api/jsonws/user/get-user-by-id")
				.query("userId", "10194")
				.basicAuthentication("test", "test")
				.send();

		System.out.println(response);
	}

	private static void sendGet15() {
		HttpResponse response = HttpRequest
				.get("http://192.168.1.111:8080/api/jsonws/user/get-user-by-id?userId=10194")
				.basicAuthentication("test", "test")
				.send();

		System.out.println(response);
	}

	private static void sendGet2() {
		HttpRequest request = new HttpRequest();

		request
				.method("GET")
				.protocol("http")
				.host("192.168.1.111")
				.port(8080)
				.path("/api/jsonws/user/get-user-by-id");

		Map<String, Object> httpParams = request.query();
		httpParams.put("userId", "10194");

		request.basicAuthentication("test", "test");

		Socket socket = request.open().getSocket();
		try {
			socket.setSoTimeout(1000);
		} catch (SocketException e) {
		}

		HttpResponse response = request.send();

		System.out.println(response);
	}

	private static void sendPost1() {
		HttpResponse response = HttpRequest
				.post("http://192.168.1.111:8080/api/jsonws/user/get-user-by-id")
				.form("userId", "10194")
				.basicAuthentication("test", "test")
				.send();

		System.out.println(response);
	}

	private static void sendPost2() {
		HttpRequest httpRequest = HttpRequest
				.post("http://192.168.1.111:8080/api/jsonws/dlapp/add-file-entry")
				.form(
						"repositoryId", "10178",
						"folderId", "11219",
						"sourceFileName", "a.zip",
						"mimeType", "application/zip",
						"title", "test",
						"description", "Upload test",
						"changeLog", "testing...",
						"file", new File("d:\\a.jpg.zip")
				)
				.basicAuthentication("test", "test");

		System.out.println(httpRequest);

		HttpResponse httpResponse = httpRequest.send();

		System.out.println(httpResponse);
	}

	private static void sendInvoker() {
		HttpResponse response = HttpRequest
				.get("http://192.168.1.111:8080/api/jsonws/invoke")
				.body("{'$user[userId, screenName] = /user/get-user-by-id' : {'userId':'10194'}}")
				.basicAuthentication("test", "test")
				.send();

		System.out.println(response);
	}

	public static void main(String[] args) throws IOException {
//		getJodd();
//		getLiferayGZip();
//		getGoogles();
//		postGoogle();
//		sendGet1();
//		sendGet15();
//		sendGet2();
//		sendPost1();
//		sendPost2();
//		sendInvoker();
	}

	private static void getJodd() {
		HttpRequest httpRequest = HttpRequest.get("http://jodd.org");
		HttpResponse response = httpRequest.send();

		System.out.println(response);
	}

	private static void getLiferayGZip() {
		HttpResponse response = HttpRequest
				.get("http://www.liferay.com")
				.acceptEncoding("gzip")
				.send();

		System.out.println(response.unzip());
	}

	private static void getGoogles() {
		HttpRequest httpRequest = HttpRequest.get("https://jodd.org");
		HttpResponse response = httpRequest.send();

		System.out.println(response);
	}

	private static void postGoogle() {
		HttpRequest httpRequest = HttpRequest.post("http://www.google.com");
		HttpResponse response = httpRequest.send();

		System.out.println(response);
	}


}