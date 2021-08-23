/*******************************************************************************************************
 *
 * msi.gaml.types.GamaAttributesType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.types;

import java.util.Map;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.type;
import gama.metamodel.agent.IAgent;
import gama.metamodel.agent.SavedAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaMapFactory;
import gama.util.IMap;
import gaml.expressions.IExpression;

@type (
		name = IKeyword.ATTRIBUTES,
		id = IType.ATTRIBUTES,
		wraps = { SavedAgent.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.MAP },
		doc = @doc ("Represents the attributes of an agent, a special kind of map<string, unknown> with additional information such as its index or its sub-populations"))

public class GamaAttributesType extends GamaMapType {

	@SuppressWarnings ("unchecked")
	@Override
	public SavedAgent cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentType, final boolean copy) throws GamaRuntimeException {
		if (obj instanceof IAgent) { return new SavedAgent(scope, (IAgent) obj); }
		if (obj instanceof SavedAgent) {
			if (copy) {
				return ((SavedAgent) obj).clone();
			} else {
				return (SavedAgent) obj;
			}
		}
		if (obj instanceof Map) {
			final IMap<String, Object> map =
					GamaMapFactory.createWithoutCasting(Types.STRING, Types.NO_TYPE, (Map<String, Object>) obj);
			return new SavedAgent(map);
		}
		return null;
	}

	@Override
	public IType keyTypeIfCasting(final IExpression exp) {
		return Types.get(STRING);
	}

}
