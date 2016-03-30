package hr.sdautovic.net.tcp.server;

import hr.sdautovic.net.tcp.connection.ConnectionHandlerFactory;

public class TCPServer {
	private ConnectionHandlerFactory connectionHandlerFactory;
	
	public TCPServer(ConnectionHandlerFactory connectionHandlerFactory) {
		this.connectionHandlerFactory = connectionHandlerFactory;
	}

	public ConnectionHandlerFactory getConnectionHandlerFactory() {
		return connectionHandlerFactory;
	}

	public void setConnectionHandlerFactory(ConnectionHandlerFactory connectionHandlerFactory) {
		this.connectionHandlerFactory = connectionHandlerFactory;
	}
}
