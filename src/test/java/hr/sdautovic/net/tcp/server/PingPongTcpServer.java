package hr.sdautovic.net.tcp.server;

import hr.sdautovic.net.tcp.connection.ConnectionHandlerFactory;

public class PingPongTcpServer extends TCPServer {

	public PingPongTcpServer(ConnectionHandlerFactory connectionHandlerFactory) {
		super(connectionHandlerFactory);
	}

}
