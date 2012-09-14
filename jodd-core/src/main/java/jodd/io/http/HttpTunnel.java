// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.http;

import jodd.io.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple HTTP tunnel base ready to be extended.
 */
public class HttpTunnel {

	/**
	 * The number of threads that can be executed in parallel.
	 */
	protected int threadPoolSize = 10;

	/**
	 * Number of incoming sockets connection that can be hold
	 * before processing each.
	 */
	protected int socketBacklog = 100;

	/**
	 * Tunnel listening port.
	 */
	protected int listenPort = 8888;

	/**
	 * Target host.
	 */
	protected String targetHost = "localhost";
	/**
	 * Target port.
	 */
	protected int targetPort = 8080;

	protected ExecutorService executorService;
	protected boolean running;
	protected ServerSocket serverSocket;

	/**
	 * Starts HTTP tunnel. Method ends when the tunnel is stopped.
	 */
	public void start() throws IOException {
		serverSocket = new ServerSocket(listenPort, socketBacklog);
		serverSocket.setReuseAddress(true);
		executorService = Executors.newFixedThreadPool(threadPoolSize);

		running = true;
		while (running) {
			Socket socket = serverSocket.accept();
			socket.setKeepAlive(false);
			executorService.execute(onSocketConnection(socket));
		}
		executorService.shutdown();
	}

	/**
	 * Invoked on incoming connection. By default returns {@link HttpTunnelConnection}
	 * to handle the connection. May be used to return custom
	 * handlers.
	 */
	protected Runnable onSocketConnection(Socket socket) {
		return new HttpTunnelConnection(socket);
	}

	/**
	 * Stops the tunnel, shutdowns the thread pool and closes server socket.
	 */
	public void stop() {
		running = false;
		executorService.shutdown();
		try {
			serverSocket.close();
		} catch (IOException ignore) {
		}
	}

	/**
	 * Single connection handler that performs the tunneling.
	 */
	public class HttpTunnelConnection implements Runnable {

		protected final Socket socket;

		public HttpTunnelConnection(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				tunnel();
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
		}

		/**
		 * Invoked after income connection is parsed. Nothing is
		 * changed in the request. Sometimes, it make sense to
		 * modify the "Host" header to match the target.
		 */
		protected void onRequest(HttpTransfer request) {
		}

		/**
		 * Invoked after target response is processed. Response is now
		 * ready to be sent back to the client. The following header
		 * parameters are changed:
		 * <li>Transfer-Encoding is removed, as body is returned at once,
		 * <li>Content-Length is added/update to body size.
		 */
		protected void onResponse(HttpTransfer response) {
		}

		/**
		 * Performs the tunneling. The following steps occurs:
		 * <li>read and parse clients request
		 * <li>open socket to target
		 * <li>resend request to target
		 * <li>read targets response
		 * <li>fix response and resend it to client
		 */
		protected void tunnel() throws IOException {

			// read request
			InputStream socketInput = socket.getInputStream();
			HttpTransfer request = Http.readRequest(socketInput);

			// open client socket to target
			Socket clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(targetHost, targetPort));

			// do request
			onRequest(request);

			// resend request to target
			OutputStream out = clientSocket.getOutputStream();
			request.send(out);

			// read target response
			InputStream in = clientSocket.getInputStream();
			HttpTransfer response = Http.readResponse(in);

			// close client socket
			StreamUtil.close(in);
			StreamUtil.close(out);
			clientSocket.close();

			// fix response
			if (response.getBody() != null) {
				response.removeHeader("Transfer-Encoding");
				response.addHeader("Content-Length", response.getBody().length);
			}

			// do response
			onResponse(response);

			// send response back
			OutputStream socketOutput = socket.getOutputStream();
			response.send(socketOutput);

			// close socket
			StreamUtil.close(socketInput);
			StreamUtil.close(socketOutput);
			socket.close();
		}
	}

}