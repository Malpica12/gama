/*********************************************************************************************
 *
 * 'GamaMatrixReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ext.serialize.gamaType.reduced;

import gama.metamodel.shape.GamaPoint;
import gama.runtime.IScope;
import gama.util.IList;
import gama.util.matrix.GamaMatrix;
import gaml.types.GamaMatrixType;
import gaml.types.IType;

@SuppressWarnings({ "rawtypes" })
public class GamaMatrixReducer {
	private final IType contentTypeMatrixReducer;
	private final IList valuesMatrixReducer;
	private final int nRows;
	private final int nCols;

	public GamaMatrixReducer(final IScope scope, final GamaMatrix m) {
		contentTypeMatrixReducer = m.getGamlType().getContentType();
		nRows = m.getRows(null);
		nCols = m.getCols(null);
		valuesMatrixReducer = m.listValue(scope, contentTypeMatrixReducer, true);
	}

	public GamaMatrix constructObject(final IScope scope) {
		return (GamaMatrix) GamaMatrixType.from(scope, valuesMatrixReducer, contentTypeMatrixReducer,
				new GamaPoint(nCols, nRows));

	}
}
