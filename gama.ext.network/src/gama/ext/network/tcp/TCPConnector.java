/*******************************************************************************************************
 *
 * TCPConnector.java, in gama.ext.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.network.tcp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gama.ext.network.common.Connector;
import gama.ext.network.common.ConnectorMessage;
import gama.ext.network.common.GamaNetworkException;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IList;
import gaml.operators.Cast;

/**
 * The Class TCPConnector.
 */
@SuppressWarnings ({ "unchecked" })
public class TCPConnector extends Connector {
	
	/** The is server. */
	private final boolean is_server = false;
	
	/** The  tcp server. */
	public static String _TCP_SERVER = "__tcp_server";
	
	/** The  tcp socket. */
	public static String _TCP_SOCKET = "__tcp_socket";
	
	/** The  tcp client. */
	public static String _TCP_CLIENT = "__tcp_client";
	
	/** The  tcp so timeout. */
	public static Integer _TCP_SO_TIMEOUT = 100;

	/** The default host. */
	public static String DEFAULT_HOST = "localhost";
	
	/** The default port. */
	public static String DEFAULT_PORT = "1988";

	/**
	 * Open server socket.
	 *
	 * @param agent the agent
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void openServerSocket(final IAgent agent) throws GamaRuntimeException {
		final Integer port = Cast.asInt(agent.getScope(), this.getConfigurationParameter(SERVER_PORT));
		if (agent.getScope().getSimulation().getAttribute(_TCP_SERVER + port) == null) {
			try {
				final ServerSocket sersock = new ServerSocket(port);
				sersock.setSoTimeout(_TCP_SO_TIMEOUT);
				final MultiThreadedSocketServer ssThread = new MultiThreadedSocketServer(agent, sersock);
				ssThread.start();
				agent.getScope().getSimulation().setAttribute(_TCP_SERVER + port, ssThread);

			} catch (final BindException be) {
				throw GamaRuntimeException.create(be, agent.getScope());
			} catch (final Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}

	}

	/**
	 * Connect to server socket.
	 *
	 * @param agent the agent
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void connectToServerSocket(final IAgent agent) throws GamaRuntimeException {
		final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_SOCKET);
		Socket sock = null;
		if (c != null) {
			sock = c.getMyClientSocket();
		}
		if (sock == null) {
			try {
				String server = this.getConfigurationParameter(SERVER_URL);
				String port = this.getConfigurationParameter(SERVER_PORT);
				server = server == null ? DEFAULT_HOST : server;
				port = port == null ? DEFAULT_PORT : port;
				sock = new Socket(server, Cast.asInt(agent.getScope(), port));
				sock.setSoTimeout(_TCP_SO_TIMEOUT);

				final ClientServiceThread cSock = new ClientServiceThread(agent, sock);
				cSock.start();
				agent.setAttribute(_TCP_SOCKET, cSock);
				// return sock.toString();
			} catch (final Exception e) {
				throw GamaRuntimeException.create(e, agent.getScope());
			}
		}
	}

	@Override
	protected void connectToServer(final IAgent agent) throws GamaNetworkException {
		if (is_server) {
			openServerSocket(agent);
		} else {
			connectToServerSocket(agent);
		}
	}

	/**
	 * Send to client.
	 *
	 * @param agent the agent
	 * @param cli the cli
	 * @param data the data
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void sendToClient(final IAgent agent, final String cli, final String data) throws GamaRuntimeException {
		try {
			final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_CLIENT + cli);
			Socket sock = null;
			if (c != null) {
				sock = c.getMyClientSocket();
			}
			if (sock == null) { return; }
			final OutputStream ostream = sock.getOutputStream();
			final PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(data);
			pwrite.flush();
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}
	}

	/**
	 * Send to server.
	 *
	 * @param agent the agent
	 * @param data the data
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void sendToServer(final IAgent agent, final String data) throws GamaRuntimeException {
		OutputStream ostream = null;
		final ClientServiceThread c = (ClientServiceThread) agent.getAttribute(_TCP_SOCKET);
		Socket sock = null;
		if (c != null) {
			sock = c.getMyClientSocket();
		}
		if (sock == null || sock.isClosed() || sock.isInputShutdown()) { return; }
		try {
			ostream = sock.getOutputStream();
			final PrintWriter pwrite = new PrintWriter(ostream, true);
			pwrite.println(data);
			pwrite.flush();
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, agent.getScope());
		}

	}

	// @Override
	// public void send(final IAgent sender, final String receiver, final
	// GamaMessage content) {
	// if (is_server) {
	// sendToClient(sender, receiver, content.getContents(myScope));
	// } else {
	// sendToServer(sender, content.getContents(myScope));
	// }
	// }

	@Override
	public List<ConnectorMessage> fetchMessageBox(final IAgent agent) {
		// TODO Auto-generated method stub
		return super.fetchMessageBox(agent);
	}

	@Override
	public Map<IAgent, LinkedList<ConnectorMessage>> fetchAllMessages() {
		for (final IAgent agt : this.receivedMessage.keySet()) {
			// IScope scope = agt.getScope();
			final IList<ConnectorMessage> m = (IList<ConnectorMessage>) agt.getAttribute("messages" + agt);
			if (m != null) {
				// receivedMessage.get(agt).addAll(m);
				for (final ConnectorMessage cm : m) {
					receivedMessage.get(agt).add(cm);
				}
				m.clear();
				agt.setAttribute("message" + agt, m);
			}
		}
		return super.fetchAllMessages();
	}

	@Override
	protected void subscribeToGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unsubscribeGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isAlive(final IAgent agent) throws GamaNetworkException {
		final String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(agent.getScope(), sport);
		final Thread sersock = (Thread) agent.getScope().getSimulation().getAttribute(_TCP_SERVER + port);
		if (sersock != null && sersock.isAlive()) { return true; }

		final Thread cSock = (Thread) agent.getScope().getAgent().getAttribute(_TCP_SOCKET);
		if (sersock != null && cSock.isAlive()) { return true; }

		return false;
	}

	@Override
	protected void releaseConnection(final IScope scope) throws GamaNetworkException {
		// final String server = this.getConfigurationParameter(SERVER_URL);
		final String sport = this.getConfigurationParameter(SERVER_PORT);
		final Integer port = Cast.asInt(scope, sport);
		final Thread sersock = (Thread) scope.getSimulation().getAttribute(_TCP_SERVER + port);
		final Thread cSock = (Thread) scope.getAgent().getAttribute(_TCP_SOCKET);

		try {
			if (sersock != null) {
				sersock.interrupt();
				scope.getSimulation().setAttribute(_TCP_SERVER + port, null);
			}
			if (cSock != null) {
				cSock.interrupt();
				scope.getAgent().setAttribute(_TCP_SOCKET, null);
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	@Override
	protected void sendMessage(final IAgent sender, final String receiver, final String cont)
			throws GamaNetworkException {

		String content = cont.replaceAll("\b\r", "@b@@r@");
		content = content.replaceAll("\n", "@n@");
		if (is_server) {
			sendToClient(sender, receiver, content);
		} else {
			sendToServer(sender, content);
		}
		// if(is_server){
		// primSendToClient(sender, receiver, content);
		// }else{
		// primSendToServer(sender, content);
		// }
	}
}
