/*********************************************************************************************
 *
 * 'GamaMapReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.serialize.gamaType.reduced;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import gama.ext.serialize.gamaType.reference.ReferenceMap;
import gama.kernel.simulation.SimulationAgent;
import gama.runtime.IScope;
import gama.util.GamaMapFactory;
import gama.util.IMap;
import gama.util.IReference;
import gaml.types.IType;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMapReducer {
	private final IType keysType;
	private final IType dataType;
	// private ArrayList<GamaPair> values = new ArrayList<GamaPair>();
	private Map<Object, Object> valuesMapReducer = new HashMap();

	public GamaMapReducer(final IMap m) {
		keysType = m.getGamlType().getKeyType();
		dataType = m.getGamlType().getContentType();
		m.forEach((k, v) -> valuesMapReducer.put(k, v));
	}

	public IMap constructObject(final IScope scope) {

		boolean isReference = false;
		final Iterator ite = valuesMapReducer.entrySet().iterator();
		while (!isReference && ite.hasNext()) {
			final Entry e = (Entry) ite.next();
			isReference = IReference.isReference(e.getKey()) || IReference.isReference(e.getValue());
		}

		return isReference ? new ReferenceMap(this)
				: GamaMapFactory.create(scope, keysType, dataType, valuesMapReducer);

	}

	public void unreferenceReducer(final SimulationAgent sim) {

		final HashMap<Object, Object> mapWithoutReferences = new HashMap<>();

		for (final Entry e : valuesMapReducer.entrySet()) {
			mapWithoutReferences.put(IReference.getObjectWithoutReference(e.getKey(), sim),
					IReference.getObjectWithoutReference(e.getValue(), sim));
		}

		valuesMapReducer = mapWithoutReferences;
	}

	public IType getKeysType() {
		return keysType;
	}

	public IType getDataType() {
		return dataType;
	}

	public Map<Object, Object> getValues() {
		return valuesMapReducer;
	}

	public void setValues(final HashMap m) {
		valuesMapReducer = m;
	}
}
