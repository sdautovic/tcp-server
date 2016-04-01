package hr.sdautovic.net.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(ServerThread.class);
	
	private static final int DEFAULT_MAX_CONNECTIONS = 100;
	
	protected int maxConnections;
	protected boolean running;
	
	private ServerSocket serverSocket;
	
	private final Semaphore connectionPermits;
	private final Set<Session> sessionThreads;
	
	private final TCPServer server;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	public ServerThread(TCPServer server, ServerSocket socket) throws UnknownHostException, IOException {
		this.maxConnections = DEFAULT_MAX_CONNECTIONS;
		this.running = false;
		this.connectionPermits = new Semaphore(this.maxConnections);
		this.sessionThreads = new HashSet<Session>(this.maxConnections * 4 / 3 + 1);
		this.server = server;
		this.serverSocket = socket;
	}
	
	public ServerThread(TCPServer server, ServerSocket socket, int max_connections) throws UnknownHostException, IOException {
		this.running = false;
		this.maxConnections = max_connections;
		this.connectionPermits = new Semaphore(this.maxConnections);
		this.sessionThreads = new HashSet<Session>(this.maxConnections * 4 / 3 + 1);
		this.server = server;
		this.serverSocket = socket;
	}
	
	
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void run() {
		this.running = true;
		
		while (this.running) {
			
			try
			{
				connectionPermits.acquire();
			} catch (InterruptedException consumed) {
				log.info("maximum number of allowed connections reached");
				log.error(consumed.toString());
				continue; 
			}
			
			Socket clientSocket = null;
			
			try {
				clientSocket = this.serverSocket.accept();
				
			} catch (IOException e) {
				connectionPermits.release();
				
				if (!this.running) {
					return;
				}
				
				continue;
			}
			
			/** new session implementation **/
			Session session = null;
			
			session = new Session(this.server, this, clientSocket);
			
			synchronized (this)
			{
				this.sessionThreads.add(session);
			}

			try {
				this.executorService.execute(session);
			}
			catch (RejectedExecutionException e) {
				connectionPermits.release();
				synchronized (this)
				{
					this.sessionThreads.remove(session);
				}
				log.error("error while executing a session exception=", e);
				
				try
				{
					clientSocket.close();
				} catch (IOException e1)
				{
					log.debug("cannot close socket after exception exception=", e1);
				}
				continue;
			}
		}
	}
	
	public void shutdown() {
		shutdownServerThread();
		shutdownSessions();	
	}

	private void shutdownServerThread() {
		running = true;
		closeServerSocket();
		interrupt();
		try {
			join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void closeServerSocket() {
		try
		{
			this.serverSocket.close();
			log.debug("TCP server socket shutdown");
		} catch (IOException e) {
			log.error("failed to close server socket exception=", e);
		}
	}
	

	private void shutdownSessions() {
		List<Session> sessionsToBeClosed;
		
		synchronized (this) {
			sessionsToBeClosed = new ArrayList<Session>(sessionThreads);
		}
		
		for (Session sessionThread : sessionsToBeClosed) {
			sessionThread.quit();
		}
	
		this.executorService.shutdown();
		
		try {
			this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.warn("interrupted waiting for termination of session threads exception=", e);
			Thread.currentThread().interrupt();
		}
	}

	public synchronized boolean hasTooManyConnections() {
		return sessionThreads.size() > this.maxConnections;
	}

	public synchronized int getNumberOfConnections() {
		return sessionThreads.size();
	}

	public void sessionEnded(Session session) {
		synchronized (this) {
			sessionThreads.remove(session);
		}
		connectionPermits.release();
	}
}
