/*********************************************************************************************
 *
 * 'CompositeGamaMessage.java, in plugin ummisco.gama.network, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
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

public class CompositeGamaMessage extends GamaMessage {
	protected Object deserializeContent;

	public CompositeGamaMessage(final IScope scope, final GamaMessage message) {
		super(scope, message.getSender(), message.getReceivers(), message.getContents(scope));
		this.contents = StreamConverter.convertNetworkObjectToStream(scope, message.getContents(scope));
		this.emissionTimeStamp = message.getEmissionTimestamp();
		this.setUnread(true);
		deserializeContent = null;
	}

	private CompositeGamaMessage(final IScope scope, final Object sender, final Object receivers, final Object content,
			final Object deserializeContent, final int timeStamp) {
		super(scope, sender, receivers, content);
		this.emissionTimeStamp = timeStamp;
		this.setUnread(true);
		this.deserializeContent = deserializeContent;
	}

	@Override
	public Object getContents(final IScope scope) {
		this.setUnread(false);
		if (deserializeContent == null) {
			deserializeContent = StreamConverter.convertNetworkStreamToObject(scope, (String) contents);// StreamConverter.convertStreamToObject(scope,
																										// (String)(super.getContents(scope)));
		}
		return deserializeContent;
	}
}
