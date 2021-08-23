/*********************************************************************************************
 *
 * 'GamaMapConverter.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.serialize.gamaType.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import gama.ext.serialize.gamaType.reduced.GamaMapReducer;
import gama.metamodel.agent.SavedAgent;
import gama.util.IMap;

@SuppressWarnings ({ "rawtypes" })
public class GamaMapConverter implements Converter {

	ConverterScope convertScope;

	public GamaMapConverter(final ConverterScope s) {
		convertScope = s;
	}

	@Override
	public boolean canConvert(final Class arg0) {
		if (IMap.class.isAssignableFrom(arg0) && !SavedAgent.class.equals(arg0)) { return true; }
		return false;
	}

	@Override
	public void marshal(final Object arg0, final HierarchicalStreamWriter writer, final MarshallingContext arg2) {
		final IMap mp = (IMap) arg0;
		// GamaMapReducer m = new GamaMapReducer(mp);
		// writer.startNode("GamaMap");
		//
		// writer.startNode("KeysType");
		// arg2.convertAnother(m.getKeysType());
		// writer.endNode();
		//
		// writer.startNode("ValueType");
		// arg2.convertAnother(m.getDataType());
		// writer.endNode();
		//
		// for(GamaPair gm : m.getValues()) {
		// arg2.convertAnother(gm);
		// }
		//
		// writer.endNode();

		arg2.convertAnother(new GamaMapReducer(mp));

	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext arg1) {
		// reader.moveDown();
		final GamaMapReducer rmt = (GamaMapReducer) arg1.convertAnother(null, GamaMapReducer.class);
		// reader.moveUp();
		return rmt.constructObject(convertScope.getScope());
	}

}
