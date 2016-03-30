package hr.sdautovic.net.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import hr.sdautovic.net.tcp.connection.ConnectionContext;
import hr.sdautovic.net.tcp.connection.ConnectionHandler;
import hr.sdautovic.net.tcp.connection.ConnectionHandlerFactory;

public class PingPongTcpServerApplication {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		PingPongTcpServer server = new PingPongTcpServer(new ConnectionHandlerFactory() {	
			public ConnectionHandler create(ConnectionContext ctx) {
				return new ConnectionHandler() {
					public void handleConnection(Socket client_socket, ServerSocket server_socket) {
						Date date = new Date();
						String output = "";
						try {
							BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
							String input = in.readLine();
							output = "PONG: [" + input + "] current_date=" + date.toString() + "\n";
							client_socket.getOutputStream().write(output.getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
			}
		});
		ServerSocket serverSocket = new ServerSocket(9000, 0, InetAddress.getByName("0.0.0.0"));
		
		ServerThread serverThread = new ServerThread(server, serverSocket, 1);
		serverThread.start();
	}
}
