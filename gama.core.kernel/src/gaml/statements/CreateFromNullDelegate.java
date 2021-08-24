/*******************************************************************************************************
 *
 * CreateFromNullDelegate.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.statements;

import java.util.List;
import java.util.Map;

import gama.common.interfaces.ICreateDelegate;
import gama.runtime.IScope;
import gama.util.GamaMapFactory;
import gaml.types.IType;
import gaml.types.Types;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class CreateFromNullDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 *
	 * @see gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		return source == null;
	}

	/**
	 * Method createFrom() reads initial values decribed by the modeler (facet with)
	 *
	 * @author Alexis Drogoul
	 * @since 04-09-2012
	 * @see gama.common.interfaces.ICreateDelegate#createFrom(gama.runtime.IScope, java.util.List, int,
	 *      java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object input, final Arguments init, final CreateStatement statement) {
		Map<String, Object> nullMap = null;
		if (init == null) {
			nullMap = GamaMapFactory.create();
		}
		final int num = max == null ? 1 : max;
		for (int i = 0; i < num; i++) {
			final Map<String, Object> map =
					init == null ? nullMap : GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
			statement.fillWithUserInit(scope, map);
			inits.add(map);
		}
		return true;
	}

	/**
	 * Method fromFacetType()
	 * 
	 * @see gama.common.interfaces.ICreateDelegate#fromFacetType()
	 */
	@Override
	public IType<?> fromFacetType() {
		return Types.NO_TYPE; // Only delegate allowed to do this
	}

}
