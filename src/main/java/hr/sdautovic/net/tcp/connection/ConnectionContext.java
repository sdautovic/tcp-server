package hr.sdautovic.net.tcp.connection;

import java.net.ServerSocket;
import java.net.Socket;

public interface ConnectionContext {
	public Socket getClientSocket();
	public ServerSocket getServerSocket();
}
