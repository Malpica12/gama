/*******************************************************************************************************
 *
 * CommandMessage.java, in gama.ext.network, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.network.common;

/**
 * The Class CommandMessage.
 */
public class CommandMessage extends NetworkMessage {
	
	/**
	 * The Enum CommandType.
	 */
	public enum CommandType
	{
		
		/** The new group. */
		NEW_GROUP,
		
		/** The remove group. */
		REMOVE_GROUP
	}
	
	/** The command. */
	private CommandType command;
	
	/**
	 * Instantiates a new command message.
	 *
	 * @param from the from
	 * @param to the to
	 * @param cmd the cmd
	 * @param data the data
	 */
	public CommandMessage(final String from, final String to,final CommandType cmd, final String data) {
		super(from, to, data);
		this.command = cmd;
		this.isPlainMessage=true;
	}
	
	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public CommandType getCommand()
	{
		return this.command;
	}
	@Override
	public boolean isCommandMessage() {
		return true;
	}
	
}