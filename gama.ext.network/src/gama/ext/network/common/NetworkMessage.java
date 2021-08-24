/*********************************************************************************************
 *
 * 'NetworkMessage.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.network.common;

import gama.ext.serialize.factory.StreamConverter;
import gama.runtime.IScope;
import gaml.extensions.messaging.GamaMessage;

public class NetworkMessage implements ConnectorMessage {
	public static final String UNDEFINED = "undefined";

	private final String from;
	private final String to;
	private final String content;
	protected boolean isPlainMessage = false;

	protected NetworkMessage(final String to, final String data) {
		this.content = data;
		this.from = UNDEFINED;
		this.to = to;
		isPlainMessage = true;
	}

	protected NetworkMessage(final String from, final String to, final String data) {
		this.from = from;
		this.to = to;
		this.content = data;
		isPlainMessage = false;
	}

	@Override
	public String getSender() {
		return from;
	}

	@Override
	public String getReceiver() {
		return to;
	}

	@Override
	public String getPlainContents() {
		return content;
	}

	@Override
	public boolean isPlainMessage() {
		return isPlainMessage;
	}

	@Override
	public GamaMessage getContents(final IScope scope) {
		return isPlainMessage ? getPlainContent(scope) : getCompositeContent(scope);
	}

	public GamaMessage getPlainContent(final IScope scope) {
		final GamaMessage message = new GamaMessage(scope, from, to, content);
		message.hasBeenReceived(scope);
		return message;
	}

	public GamaMessage getCompositeContent(final IScope scope) {
		final Object messageContent = StreamConverter.convertStreamToObject(scope, content);
		GamaMessage message = null;
		if (messageContent instanceof CompositeGamaMessage) {
			message = (GamaMessage) messageContent;
		} else {
			message = new GamaMessage(scope, from, to, messageContent);
		}
		message.hasBeenReceived(scope);
		return message;
	}

	@Override
	public boolean isCommandMessage() {
		return false;
	}
}
