package hr.sdautovic.net.tcp.connection;

import java.net.ServerSocket;
import java.net.Socket;

public interface ConnectionHandler {
	public void handleConnection(Socket client_socket, ServerSocket server_socket);
}
