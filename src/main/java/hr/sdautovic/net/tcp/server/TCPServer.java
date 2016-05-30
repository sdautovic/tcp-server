package hr.sdautovic.net.tcp.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.sdautovic.net.tcp.connection.ConnectionHandlerFactory;

public class TCPServer {
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private ConnectionHandlerFactory connectionHandlerFactory;
	private int soTimeout = 0;
	
	public TCPServer(ConnectionHandlerFactory connectionHandlerFactory) {
		this.connectionHandlerFactory = connectionHandlerFactory;
	}

	public ConnectionHandlerFactory getConnectionHandlerFactory() {
		return connectionHandlerFactory;
	}

	public void setConnectionHandlerFactory(ConnectionHandlerFactory connectionHandlerFactory) {
		this.connectionHandlerFactory = connectionHandlerFactory;
	}
	
	public ExecutorService getExecutorService() {
		return this.executorService;
	}
	
	public void setSoTimeout(int timeout) {
		this.soTimeout = timeout;
	}
	
	public int getSoTimeout() {
		return this.soTimeout;
	}
}
