package hr.sdautovic.net.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.sdautovic.net.tcp.connection.ConnectionContext;
import hr.sdautovic.net.tcp.connection.ConnectionHandler;

public class Session implements Runnable, ConnectionContext {
	private final static Logger log = LoggerFactory.getLogger(Session.class);
	
	/** link to parent server **/
	private TCPServer server;
	
	/** link to parent server thread **/
	private ServerThread serverThread;
	
	/** I/O to the client **/
	private Socket socket;
	ConnectionHandler conectionHandler;
	
	public Session(TCPServer server, ServerThread serverThread, Socket socket) {
		this.server = server;
		this.serverThread = serverThread;
		this.socket = socket;
	}
	
	public Socket getClientSocket() {
		return this.socket;
	}

	public ServerSocket getServerSocket() {
		return this.serverThread.getServerSocket();
	}

	public void run() {
		this.conectionHandler = this.server.getConnectionHandlerFactory().create(this);
		this.conectionHandler.handleConnection(this.socket, this.getServerSocket());
		
		this.closeConnection();
		this.serverThread.sessionEnded(this);
	}
	
	private void closeConnection() {
		try
		{
			try
			{
				this.socket.getInputStream().close();
				this.socket.getOutputStream().close();
				
			} finally {
				this.socket.close();
			}
		}
		catch (IOException e) {
			
			try {
				this.socket.close();
			} catch (IOException e1) { }
			
			log.error("closing connection from peer " + this.socket.getInetAddress().toString() + ":" + this.socket.getPort());
		}
	}
	
	public void quit() {
		this.closeConnection();
	}
}
