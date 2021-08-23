/*********************************************************************************************
 *
 * 'MultiThreadedSocketServer.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.network.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import gama.core.dev.utils.DEBUG;
import gama.ext.network.skills.INetworkSkill;
import gama.metamodel.agent.IAgent;
import gama.util.IList;
import gaml.operators.Cast;

public class MultiThreadedSocketServer extends Thread {

	static {
		DEBUG.ON();
	}

	private final IAgent myAgent;
	private ServerSocket myServerSocket;
	private boolean closed = false;

	/**
	 * @return the myServerSocket
	 */
	public ServerSocket getMyServerSocket() {
		return myServerSocket;
	}

	/**
	 * @param myServerSocket
	 *            the myServerSocket to set
	 */
	public void setMyServerSocket(final ServerSocket myServerSocket) {
		this.myServerSocket = myServerSocket;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		closed = true;
		super.interrupt();
	}

	public MultiThreadedSocketServer(final IAgent a, final ServerSocket ss) {
		myAgent = a;
		myServerSocket = ss;
	}

	@Override
	public void run() {
		// Successfully created Server Socket. Now wait for connections.
		while (!closed) {
			try {
				// Accept incoming connections.
				if (myAgent.dead()) {
					closed = true;
					this.interrupt();
					return;
				}
				// DEBUG.OUT(myServerSocket+" server waiting for connection");

				final Socket clientSocket = myServerSocket.accept();
				DEBUG.OUT(clientSocket + " connected");

				if (!clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
					final IList<String> list_net_agents =
							Cast.asList(myAgent.getScope(), myAgent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
					if (list_net_agents != null && !list_net_agents.contains(clientSocket.toString())) {
						list_net_agents.addValue(myAgent.getScope(), clientSocket.toString());
						myAgent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, list_net_agents);
						clientSocket.setSoTimeout(TCPConnector._TCP_SO_TIMEOUT);
						clientSocket.setKeepAlive(true);

						final ClientServiceThread cliThread = new ClientServiceThread(myAgent, clientSocket);
						cliThread.start();

						myAgent.setAttribute(TCPConnector._TCP_CLIENT + clientSocket.toString(), cliThread);
					}
				}

			} catch (final SocketTimeoutException ste) {
				// DEBUG.OUT("server waiting time out ");
				// try {
				// Thread.sleep(1000);
				// } catch(InterruptedException ie){
				// }
				// catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} catch (final Exception ioe) {

				if (myServerSocket.isClosed()) {
					closed = true;
				} else {
					ioe.printStackTrace();
				}
			}
		}
		// DEBUG.OUT("closed ");
		try {
			myAgent.setAttribute(TCPConnector._TCP_SERVER + myServerSocket.getLocalPort(), null);
			myServerSocket.close();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}