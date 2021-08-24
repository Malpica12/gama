/*******************************************************************************************************
 *
 * TCPConnection.java, in gama.ext.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.network.tcp;

import java.io.IOException;
import java.util.ArrayList;

import gama.ext.network.common.CommandMessage;
import gama.ext.network.common.Connector;
import gama.ext.network.common.GamaNetworkException;
import gama.ext.network.common.MessageFactory;
import gama.ext.network.common.CommandMessage.CommandType;
import gama.ext.network.common.MessageFactory.MessageType;
import gama.ext.network.common.socket.SocketService;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;

/**
 * The Class TCPConnection.
 */
public class TCPConnection extends Connector {

	/** The socket. */
	private SocketService socket;
	
	/** The is server. */
	private final boolean isServer;

	/** The remote box name. */
	private final ArrayList<String> remoteBoxName;

	/**
	 * Instantiates a new TCP connection.
	 *
	 * @param scope the scope
	 * @param isServer the is server
	 */
	public TCPConnection(final IScope scope, final boolean isServer) {
		this.isServer = isServer;
		this.remoteBoxName = new ArrayList<>();
	}

	/**
	 * Extract and apply command.
	 *
	 * @param sender the sender
	 * @param message the message
	 */
	protected void extractAndApplyCommand(final String sender, final String message) {
		final CommandMessage mm = MessageFactory.unPackCommandMessage(sender, message);
		final String sttr = mm.getPlainContents();
		if (mm.getCommand().equals(CommandType.NEW_GROUP)) {
			this.remoteBoxName.add(sttr);
		}

		if (mm.getCommand().equals(CommandType.REMOVE_GROUP)) {
			this.remoteBoxName.remove(sttr);
		}
	}

	@Override
	protected void connectToServer(final IAgent agent) throws GamaNetworkException {
		if (isConnected) { return; }

		final String server = this.getConfigurationParameter(SERVER_URL);
		final int port = Integer.valueOf(this.getConfigurationParameter(SERVER_PORT)).intValue();
		if (this.isServer) {
			socket = new ServerService(port) {
				@Override
				public void receivedMessage(final String sender, final String message) {
					final MessageType mte = MessageFactory.identifyMessageType(message);
					if (mte.equals(MessageType.COMMAND_MESSAGE)) {
						extractAndApplyCommand(sender, message);
					} else {
						final String r = MessageFactory.unpackReceiverName(message);
						storeMessage(r, message);
					}
				}
			};
		} else {
			socket = new ClientService(server, port, this) {
				@Override
				public void receivedMessage(final String sender, final String message) {
					final MessageType mte = MessageFactory.identifyMessageType(message);
					if (mte.equals(MessageType.COMMAND_MESSAGE)) {
						extractAndApplyCommand(sender, message);
					} else {
						final String rer = MessageFactory.unpackReceiverName(message);
						storeMessage(rer, message);
					}
				}
			};
		}
		try {
			socket.startService();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.setConnected();
	}

	@Override
	protected boolean isAlive(final IAgent agent) throws GamaNetworkException {
		return socket.isOnline();
	}

	@Override
	protected void subscribeToGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		if (!this.localMemberNames.containsKey(boxName)) {
			this.remoteBoxName.add(boxName);
		}

		final CommandMessage cmd = MessageFactory.buildCommandMessage(socket.getLocalAddress(),
				socket.getRemoteAddress(), CommandType.NEW_GROUP, boxName);
		this.sendMessage(agt, socket.getRemoteAddress(), MessageFactory.packMessage(cmd));
	}

	@Override
	protected void unsubscribeGroup(final IAgent agt, final String boxName) throws GamaNetworkException {
		this.remoteBoxName.remove(boxName);
		final CommandMessage cmd = MessageFactory.buildCommandMessage(socket.getLocalAddress(),
				socket.getRemoteAddress(), CommandType.REMOVE_GROUP, boxName);
		this.sendMessage(agt, socket.getRemoteAddress(), MessageFactory.packMessage(cmd));
	}

	@Override
	protected void releaseConnection(final IScope scope) throws GamaNetworkException {
		socket.stopService();
		socket = null;
		this.isConnected = false;
	}

	@Override
	protected void sendMessage(final IAgent sender, final String receiver, final String content)
			throws GamaNetworkException {
		try {
			if (socket != null) {
				socket.sendMessage(content);
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
