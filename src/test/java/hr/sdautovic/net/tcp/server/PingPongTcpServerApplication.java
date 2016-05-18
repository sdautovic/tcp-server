package hr.sdautovic.net.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

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
						Random rn = new Random();
						try {
							BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
							String input = in.readLine();
							try {
								Thread.sleep(rn.nextInt(10000));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							output = "PONG: [" + input + "] current_date=" + date.toString() + "\n";
							client_socket.getOutputStream().write(output.getBytes());
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
			}
		});
		ServerThread serverThread = new ServerThread(server, "0.0.0.0", 9000, 1000);
		serverThread.start();
	}
}
