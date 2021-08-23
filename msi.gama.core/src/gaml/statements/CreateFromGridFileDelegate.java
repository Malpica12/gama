/*******************************************************************************************************
 *
 * msi.gaml.statements.CreateFromGridFileDelegate.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.statements;

import java.util.List;
import java.util.Map;

import gama.common.interfaces.ICreateDelegate;
import gama.common.interfaces.IKeyword;
import gama.metamodel.shape.IShape;
import gama.runtime.IScope;
import gama.util.file.GamaGridFile;
import gaml.types.IType;
import gaml.types.Types;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CreateFromGridFileDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 *
	 * @see gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		return source instanceof GamaGridFile;
	}

	/**
	 * Method createFrom() Method used to read initial values and attributes from a GRID file.
	 *
	 * @author Alexis Drogoul
	 * @since 04-09-2012
	 * @see gama.common.interfaces.ICreateDelegate#createFrom(gama.runtime.IScope, java.util.List, int,
	 *      java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object input, final Arguments init, final CreateStatement statement) {
		final GamaGridFile file = (GamaGridFile) input;
		final int num = max == null ? file.length(scope) : Math.min(file.length(scope), max);
		for (int i = 0; i < num; i++) {
			final IShape g = file.get(scope, i);
			final Map map = g.getOrCreateAttributes();
			// The shape is added to the initial values
			g.setAttribute(IKeyword.SHAPE, g);
			// GIS attributes are mixed with the attributes of agents
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
	public IType fromFacetType() {
		return Types.FILE;
	}

}
