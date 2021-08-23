/*********************************************************************************************
 *
 * 'NetworkSkill.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.network.skills;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.GamlAnnotations.action;
import gama.core.dev.annotations.GamlAnnotations.arg;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.skill;
import gama.core.dev.annotations.GamlAnnotations.variable;
import gama.core.dev.annotations.GamlAnnotations.vars;
import gama.core.dev.utils.DEBUG;
import gama.ext.network.common.ConnectorMessage;
import gama.ext.network.common.IConnector;
import gama.ext.network.mqtt.MQTTConnector;
import gama.ext.network.serial.ArduinoConnector;
import gama.ext.network.tcp.TCPConnection;
import gama.ext.network.udp.UDPConnector;
import gama.extensions.messaging.GamaMailbox;
import gama.extensions.messaging.GamaMessage;
import gama.extensions.messaging.MessagingSkill;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaListFactory;
import gama.util.IList;
import gaml.operators.Cast;
import gaml.types.IType;

@vars ({ @variable (
		name = INetworkSkill.NET_AGENT_NAME,
		type = IType.STRING,
		doc = @doc ("Net ID of the agent")),
		@variable (
				name = INetworkSkill.NET_AGENT_GROUPS,
				type = IType.LIST,
				doc = @doc ("The set of groups the agent belongs to")),
		@variable (
				name = INetworkSkill.NET_AGENT_SERVER,
				type = IType.LIST,
				doc = @doc ("The list of all the servers to which the agent is connected")) })
@skill (
		name = INetworkSkill.NETWORK_SKILL,
		concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL })
@doc ("The " + INetworkSkill.NETWORK_SKILL
		+ " skill provides new features to let agents exchange message through network.")
public class NetworkSkill extends MessagingSkill {

	static {
		DEBUG.OFF();
	}

	final static String REGISTERED_AGENTS = "registred_agents";
	final static String REGISTRED_SERVER = "registred_servers";

	@action (
			name = "execute",
			args = { @arg (
					name = "command",
					type = IType.STRING,
					doc = @doc ("command to execute")) },
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public String systemExec(final IScope scope) {
		// final IAgent agent = scope.getAgent();
		final String commandToExecute = (String) scope.getArg("command", IType.STRING);

		// String res = "";

		Process p;
		try {
			p = Runtime.getRuntime().exec(commandToExecute);

			final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			return stdError.readLine();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	@SuppressWarnings ("unchecked")
	@action (
			name = INetworkSkill.CONNECT_TOPIC,
			args = { @arg (
					name = INetworkSkill.PROTOCOL,
					type = IType.STRING,
					doc = @doc ("protocol type (MQTT (by default), TCP, UDP): the possible value ares '"
							+ INetworkSkill.UDP_SERVER + "', '" + INetworkSkill.UDP_CLIENT + "', '"
							+ INetworkSkill.TCP_SERVER + "', '" + INetworkSkill.TCP_CLIENT
							+ "', otherwise the MQTT protocol is used.")),
					@arg (
							name = INetworkSkill.PORT,
							type = IType.INT,
							doc = @doc ("Port number")),
					@arg (
							name = INetworkSkill.WITHNAME,
							type = IType.STRING,
							optional = true,
							doc = @doc ("ID of the agent (its name) for the simulation")),
					@arg (
							name = INetworkSkill.LOGIN,
							type = IType.STRING,
							optional = true,
							doc = @doc ("login for the connection to the server")),
					@arg (
							name = INetworkSkill.PASSWORD,
							type = IType.STRING,
							optional = true,
							doc = @doc ("password associated to the login")),
					@arg (
							name = INetworkSkill.FORCE_NETWORK_USE,
							type = IType.BOOL,
							optional = true,
							doc = @doc ("force the use of the network even interaction between local agents")),
					@arg (
							name = INetworkSkill.SERVER_URL,
							type = IType.STRING,
							optional = true,
							doc = @doc ("server URL (localhost or a server URL)")),
					@arg (
							name = INetworkSkill.MAX_DATA_PACKET_SIZE,
							type = IType.INT,
							optional = true,
							doc = @doc ("For UDP connection, it sets the maximum size of received packets (deafault = 1024bits).")) },
			doc = @doc (
					value = "Action used by a networking agent to connect to a server or as a server.",
					examples = { @example (" do connect  with_name:\"any_name\";"),
							@example (" do connect to:\\\"localhost\\\" port:9876 with_name:\"any_name\";"),
							@example (" do connect to:\\\"localhost\\\" protocol:\\\"MQTT\\\" port:9876 with_name:\"any_name\";"),
							@example (" do connect to:\"localhost\" protocol:\"udp_server\" port:9876 with_name:\"Server\"; "),
							@example (" do connect to:\"localhost\" protocol:\"udp_client\" port:9876 with_name:\"Client\";"), 
							@example ("	do connect to:\"localhost\" protocol:\"udp_server\" port:9877 size_packet: 4096;")
					}))
	public void connectToServer(final IScope scope) throws GamaRuntimeException {
		if (!scope.getExperiment().hasAttribute(REGISTRED_SERVER)) {
			this.startSkill(scope);
		}
		final IAgent agt = scope.getAgent();
		final String serverURL = (String) scope.getArg(INetworkSkill.SERVER_URL, IType.STRING);
		final String login = (String) scope.getArg(INetworkSkill.LOGIN, IType.STRING);
		final String password = (String) scope.getArg(INetworkSkill.PASSWORD, IType.STRING);
		final String networkName = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		final String protocol = (String) scope.getArg(INetworkSkill.PROTOCOL, IType.STRING);
		final Boolean force_local = (Boolean) scope.getArg(INetworkSkill.FORCE_NETWORK_USE, IType.BOOL);
		final Integer port = (Integer) scope.getArg(INetworkSkill.PORT, IType.INT);
		final String packet_size = (String) scope.getArg(INetworkSkill.MAX_DATA_PACKET_SIZE, IType.STRING);

		// Fix to Issue #2618
		final String serverKey = createServerKey(serverURL, port);

		final Map<String, IConnector> myConnectors = this.getRegisteredServers(scope);
		IConnector connector = myConnectors.get(serverKey);
		if (connector == null) {

			if (protocol != null && protocol.equals(INetworkSkill.UDP_SERVER)) {
				DEBUG.OUT("create UDP server");
				connector = new UDPConnector(scope, true);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);		
				connector.configure(IConnector.PACKET_SIZE, packet_size);
			} else if (protocol != null && protocol.equals(INetworkSkill.UDP_CLIENT)) {
				DEBUG.OUT("create UDP client");
				connector = new UDPConnector(scope, false);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);
				connector.configure(IConnector.PACKET_SIZE, "" + packet_size);				
			} else if (protocol != null && protocol.equals(INetworkSkill.TCP_SERVER)) {
				DEBUG.OUT("create TCP serveur");
				connector = new TCPConnection(scope, true);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);
			} else if (protocol != null && protocol.equals(INetworkSkill.TCP_CLIENT)) {
				DEBUG.OUT("create TCP client");
				connector = new TCPConnection(scope, false);
				connector.configure(IConnector.SERVER_URL, serverURL);
				connector.configure(IConnector.SERVER_PORT, "" + port);
			} else if (protocol != null && protocol.equals("arduino")) {
				connector = new ArduinoConnector(scope);			
			} else // if(protocol.equals( INetworkSkill.MQTT))
			{
				DEBUG.OUT("create MQTT serveur " + login + " " + password);
				connector = new MQTTConnector(scope);
				if (serverURL != null) {
					connector.configure(IConnector.SERVER_URL, serverURL);
					if (port == 0) {
						connector.configure(IConnector.SERVER_PORT, "1883");
					} else {
						connector.configure(IConnector.SERVER_PORT, port.toString());

					}
					if (login != null) {
						connector.configure(IConnector.LOGIN, login);
					}
					if (password != null) {
						connector.configure(IConnector.PASSWORD, password);
					}
				}
			}
			if (force_local != null) {
				connector.forceNetworkUse(force_local.booleanValue());
			}
			// Fix to Issue #2618
			myConnectors.put(serverKey, connector);

		}

		if (agt.getAttribute(INetworkSkill.NET_AGENT_NAME) == null) {
			agt.setAttribute(INetworkSkill.NET_AGENT_NAME, networkName);
		}

		List<String> serverList = (List<String>) agt.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		if (serverList == null) {
			serverList = new ArrayList<>();
			agt.setAttribute(INetworkSkill.NET_AGENT_SERVER, serverList);
		}
		DEBUG.OUT("connector " + connector);
		connector.connect(agt);

		serverList.add(serverKey);

		// register connected agent to global groups;
		for (final String grp : INetworkSkill.DEFAULT_GROUP) {
			this.joinAGroup(scope, agt, grp);
			DEBUG.OUT(grp);
			// connector.joinAGroup(agt, grp);
		}
		;
	}

	private static String createServerKey(final String serverURL, final Integer port) {
		return serverURL + "@@" + port;
	}

	@action (
			name = INetworkSkill.FETCH_MESSAGE)
	@doc (
			value = "Fetch the first message from the mailbox (and remove it from the mailing box). If the mailbox is empty, it returns a nil message.",
			examples = { @example ("message mess <- fetch_message();"), @example ("loop while:has_more_message(){ \n"
					+ "	message mess <- fetch_message();" + "	write message.contents;" + "}") })
	public GamaMessage fetchMessage(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final GamaMailbox box = getMailbox(agent);
		GamaMessage msg = null;
		if (!box.isEmpty()) {
			msg = box.get(0);
			box.remove(0);
		}
		return msg;
	}

	@action (
			name = INetworkSkill.HAS_MORE_MESSAGE_IN_BOX)
	@doc (
			value = "Check whether the mailbox contains any message.",
			examples = { @example ("bool mailbox_contain_messages <- has_more_message();"),
					@example ("loop while:has_more_message(){ \n" + "	message mess <- fetch_message();"
							+ "	write message.contents;" + "}") })
	public boolean hasMoreMessage(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final GamaMailbox box = getMailbox(agent);
		return !box.isEmpty();
	}

	@action (
			name = INetworkSkill.REGISTER_TO_GROUP,
			args = {

					@arg (
							name = INetworkSkill.WITHNAME,
							type = IType.STRING,
							optional = false,
							doc = @doc ("name of the group")) },
			doc = @doc (
					value = "allow an agent to join a group of agents in order to broadcast messages to other members"
							+ "or to receive messages sent by other members. Note that all members of the group called : \"ALL\".",
					examples = { @example ("do join_group with_name:\"group name\";"),
							@example ("do join_group with_name:\"group name\";"
									+ "do send to:\"group name\" contents:\"I am new in this group\";") }))
	public void registerToGroup(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final String groupName = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		if (groupName != null) {
			joinAGroup(scope, agent, groupName);
		}
	}

	private IList<String> getGroups(final IScope scope, final IAgent agent) {
		IList<String> groups = Cast.asList(scope, agent.getAttribute(INetworkSkill.NET_AGENT_GROUPS));
		if (groups == null) {
			groups = GamaListFactory.create();
			agent.setAttribute(INetworkSkill.NET_AGENT_GROUPS, groups);
		}
		return groups;

	}

	public void joinAGroup(final IScope scope, final IAgent agent, final String groupName) {
		final IList<String> groups = getGroups(scope, agent);
		groups.add(groupName);

		final Collection<IConnector> connectors = getRegisteredServers(scope).values();
		for (final IConnector con : connectors) {
			con.joinAGroup(agent, groupName);
		}
	}

	@action (
			name = INetworkSkill.LEAVE_THE_GROUP,
			args = { @arg (
					name = INetworkSkill.WITHNAME,
					type = IType.STRING,
					optional = false,
					doc = @doc ("name of the group the agent wants to leave")) },
			doc = @doc (
					value = "leave a group of agents. The leaving agent will not receive any "
							+ "message from the group. Overwhise, it can send messages to the left group",
					examples = { @example (" do leave_group with_name:\"my_group\";\n") }))
	public void leaveTheGroup(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final String groupName = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		if (groupName == null) { return; }
		final IList<String> groups = getGroups(scope, agent);

		groups.remove(groupName);
		final Collection<IConnector> connectors = getRegisteredServers(scope).values();
		for (final IConnector con : connectors) {
			con.leaveTheGroup(agent, groupName);
		}

	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	protected void effectiveSend(final IScope scope, final GamaMessage message, final Object receiver) {
		if (receiver instanceof IList) {
			for (final Object o : ((IList) receiver).iterable(scope)) {
				effectiveSend(scope, message.copy(scope), o);
			}
		}
		String destName = receiver.toString();
		if (receiver instanceof IAgent && getRegisteredAgents(scope).contains(receiver)) {
			final IAgent mReceiver = (IAgent) receiver;
			destName = (String) mReceiver.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		}

		final IAgent agent = scope.getAgent();
		final List<String> serverNames = (List<String>) agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		final Map<String, IConnector> connections = getRegisteredServers(scope);
		for (final String servName : serverNames) {
			connections.get(servName).send(agent, destName, message);
		}
	}

	@action (
			name = INetworkSkill.SIMULATE_STEP,
			doc = @doc (
					value = "Simulate a step to test the skill. It must be used for Gama-platform test only",
					returns = "nothing",
					examples = { @example ("do simulate_step;\n") }))
	public void fetchMessagesOfAgents(final IScope scope) {

		for (final IConnector connection : getRegisteredServers(scope).values()) {
			final Map<IAgent, LinkedList<ConnectorMessage>> messages = connection.fetchAllMessages();
			for (final IAgent agt : messages.keySet()) {
				final GamaMailbox mailbox = (GamaMailbox) agt.getAttribute(MAILBOX_ATTRIBUTE);

				// to be check....
				/*
				 * if (!(connection instanceof MQTTConnector)) { mailbox.clear(); }
				 */
				for (final ConnectorMessage msg : messages.get(agt)) {
					mailbox.addMessage(scope, msg.getContents(scope));
				}
			}
		}
	}

	@SuppressWarnings ("unchecked")
	protected List<IAgent> getRegisteredAgents(final IScope scope) {
		return (List<IAgent>) scope.getExperiment().getAttribute(REGISTERED_AGENTS);
	}

	// private void registeredAgent(final IScope scope, final IAgent agt) {
	// getRegisteredAgents(scope).add(agt);
	// }

	@SuppressWarnings ("unchecked")
	protected Map<String, IConnector> getRegisteredServers(final IScope scope) {
		return (Map<String, IConnector>) scope.getExperiment().getAttribute(REGISTRED_SERVER);
	}

	private void initialize(final IScope scope) {

		scope.getExperiment().setAttribute(REGISTERED_AGENTS, new ArrayList<IAgent>());
		scope.getExperiment().setAttribute(REGISTRED_SERVER, new HashMap<String, IConnector>());
	}

	protected void startSkill(final IScope scope) {
		initialize(scope);
		registerSimulationEvent(scope);
	}

	private void registerSimulationEvent(final IScope scope) {
		scope.getSimulation().postEndAction(scope1 -> {
			fetchMessagesOfAgents(scope1);
			return null;
		});

		scope.getSimulation().postDisposeAction(scope1 -> {
			closeAllConnection(scope1);
			return null;
		});
	}

	private void closeAllConnection(final IScope scope) {
		for (final IConnector connection : getRegisteredServers(scope).values()) {
			connection.close(scope);
		}
		this.initialize(scope);
	}

}
