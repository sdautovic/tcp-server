package hr.sdautovic.net.tcp.connection;

public interface ConnectionHandlerFactory {
	public ConnectionHandler create(ConnectionContext ctx);
}
