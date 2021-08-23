package gama.ext.bdi;

import java.util.Map;

import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.type;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.types.GamaType;
import gaml.types.IType;

@SuppressWarnings("unchecked")
@type(name = "predicate", id = PredicateType.id, wraps = { Predicate.class }, concept = { IConcept.TYPE, IConcept.BDI })
@doc("represents a predicate")
public class PredicateType extends GamaType<Predicate> {

	public final static int id = IType.AVAILABLE_TYPES + 546654;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	@doc("cast an object as a predicate")
	public Predicate cast(final IScope scope, final Object obj, final Object val, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof Predicate) {
			return (Predicate) obj;
		}
		if (obj instanceof String) {
			return new Predicate((String) obj);
		}
		if (obj != null && obj instanceof Map) {
			final Map<String, Object> map = (Map<String, Object>) obj;
			final String nm = (String) (map.containsKey("name") ? map.get("name") : "predicate");
			final Map values = (Map) (map.containsKey("name") ? map.get("values") : null);
			return new Predicate(nm, values);
		}
		return null;
	}

	@Override
	public Predicate getDefault() {
		return null;
	}

}
