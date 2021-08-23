/*******************************************************************************************************
 *
 * msi.gaml.types.GamaMatrixType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.types;

import java.util.Arrays;
import java.util.stream.IntStream;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.type;
import gama.metamodel.shape.GamaPoint;
import gama.runtime.IScope;
import gama.runtime.concurrent.GamaExecutorService;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IContainer;
import gama.util.IList;
import gama.util.matrix.GamaFloatMatrix;
import gama.util.matrix.GamaIntMatrix;
import gama.util.matrix.GamaObjectMatrix;
import gama.util.matrix.IMatrix;
import gaml.expressions.IExpression;
import gaml.expressions.data.ListExpression;
import gaml.expressions.data.MapExpression;
import gaml.operators.Cast;

@type (
		name = IKeyword.MATRIX,
		id = IType.MATRIX,
		wraps = { IMatrix.class, GamaIntMatrix.class, GamaFloatMatrix.class, GamaObjectMatrix.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.MATRIX },
		doc = @doc ("Matrices are 2-dimensional containers that can contain any type of date (not only floats or integers). They can be accessed with a point index or by rows / columns"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMatrixType extends GamaContainerType<IMatrix> {

	public static IMatrix staticCast(final IScope scope, final Object obj, final Object param, final IType contentType,
			final boolean copy) {
		if (obj == null && param == null) return null;
		final GamaPoint size = param instanceof GamaPoint ? (GamaPoint) param : null;

		if (size == null) {
			// if (obj instanceof IFieldMatrixProvider && contentType.id() == IType.FLOAT)
			// return ((IFieldMatrixProvider) obj).getField(scope);
			if (obj instanceof IContainer) return ((IContainer) obj).matrixValue(scope, contentType, copy);
			return with(scope, obj, new GamaPoint(1, 1), contentType);
		} else if (size.x <= 0 || size.y < 0)
			throw GamaRuntimeException.error("Dimensions of a matrix should be positive.", scope);

		if (obj instanceof IContainer) return ((IContainer) obj).matrixValue(scope, contentType, size, copy);
		return with(scope, obj, size, contentType);

	}

	@Override
	public IMatrix cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, param, contentsType, copy);
	}

	public static IMatrix from(final IScope scope, final IList list, final IType desiredType,
			final GamaPoint preferredSize) {
		if (list == null || list.isEmpty()) return new GamaObjectMatrix(0, 0, desiredType);
		if (desiredType.id() == IType.INT)
			return new GamaIntMatrix(scope, list, preferredSize);
		else if (desiredType.id() == IType.FLOAT)
			return new GamaFloatMatrix(scope, list, preferredSize);
		else
			return new GamaObjectMatrix(scope, list, preferredSize, desiredType);

	}

	/**
	 * @param scope
	 *            the global scope
	 * @param matrix
	 *            the matrix to copy
	 * @param desiredType
	 *            the type of the contents of the copy
	 * @param contentsType
	 *            the type of the contents of the original
	 * @param preferredSize
	 *            the new size if any (can be null)
	 * @return
	 */
	public static IMatrix from(final IScope scope, final IMatrix matrix, final IType desiredType,
			final GamaPoint preferredSize, final boolean copy) {
		final IType contentsType = matrix.getGamlType().getContentType();
		if (!GamaType.requiresCasting(desiredType, contentsType)) return matrix.copy(scope, preferredSize, copy);
		int cols, rows;
		if (preferredSize == null) {
			cols = matrix.getCols(scope);
			rows = matrix.getRows(scope);
		} else {
			cols = (int) preferredSize.getX();
			rows = (int) preferredSize.getY();
		}
		switch (desiredType.id()) {
			case IType.INT:
				return GamaIntMatrix.from(scope, cols, rows, matrix);
			case IType.FLOAT:
				return GamaFloatMatrix.from(scope, cols, rows, matrix);
			default:
				final GamaObjectMatrix m = GamaObjectMatrix.from(cols, rows, matrix);
				final Object[] array = m.getMatrix();
				for (int i = 0; i < array.length; i++) {
					array[i] = desiredType.cast(scope, array[i], null, false);
				}
				return m;
		}

	}

	public static IMatrix with(final IScope scope, final IExpression val, final GamaPoint p)
			throws GamaRuntimeException {
		return with(scope, val, (int) p.x, (int) p.y);
	}

	public static IMatrix with(final IScope scope, final IExpression fillExpr, final int cols, final int rows) {
		IMatrix result;
		if (fillExpr == null) return new GamaObjectMatrix(cols, rows, Types.NO_TYPE);
		switch (fillExpr.getGamlType().id()) {
			case IType.FLOAT:
				result = new GamaFloatMatrix(cols, rows);
				final double[] dd = ((GamaFloatMatrix) result).getMatrix();
				if (fillExpr.isConst()) {
					Arrays.fill(dd, Cast.asFloat(scope, fillExpr.value(scope)));
				} else {
					GamaExecutorService.executeThreaded(() -> IntStream.range(0, dd.length).parallel().forEach(i -> {
						dd[i] = Cast.asFloat(scope, fillExpr.value(scope));
					}));
				}
				break;
			case IType.INT:
				result = new GamaIntMatrix(cols, rows);
				final int[] ii = ((GamaIntMatrix) result).getMatrix();
				if (fillExpr.isConst()) {
					Arrays.fill(ii, Cast.asInt(scope, fillExpr.value(scope)));
				} else {
					GamaExecutorService.executeThreaded(() -> IntStream.range(0, ii.length).parallel().forEach(i -> {
						ii[i] = Cast.asInt(scope, fillExpr.value(scope));
					}));
				}
				break;
			default:
				result = new GamaObjectMatrix(cols, rows, fillExpr.getGamlType());
				final Object[] contents = ((GamaObjectMatrix) result).getMatrix();
				if (fillExpr.isConst()) {
					Arrays.fill(contents, fillExpr.value(scope));
				} else {
					GamaExecutorService
							.executeThreaded(() -> IntStream.range(0, contents.length).parallel().forEach(i -> {
								contents[i] = fillExpr.value(scope);
							}));
				}
		}
		return result;
	}

	public static IMatrix with(final IScope scope, final Object val, final GamaPoint p, final IType contentsType)
			throws GamaRuntimeException {
		return withObject(scope, val, (int) p.x, (int) p.y, contentsType);
	}

	public static IMatrix withObject(final IScope scope, final Object val, final int cols, final int rows,
			final IType contentsType) throws GamaRuntimeException {
		if (contentsType == Types.INT || val instanceof Integer) {
			final GamaIntMatrix matrix = new GamaIntMatrix(cols, rows);
			matrix.setAllValues(scope, Types.INT.cast(scope, val, null, false));
			return matrix;
		} else if (contentsType == Types.FLOAT || val instanceof Double) {
			final GamaFloatMatrix matrix = new GamaFloatMatrix(cols, rows);
			matrix.setAllValues(scope, Types.FLOAT.cast(scope, val, null, false));
			return matrix;
		}
		final IMatrix matrix = new GamaObjectMatrix(cols, rows, contentsType);
		((GamaObjectMatrix) matrix).setAllValues(scope, contentsType.cast(scope, val, null, false));
		return matrix;
	}

	@Override
	public IType getKeyType() {
		return Types.POINT;
	}

	@Override
	public boolean isFixedLength() {
		return true;
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		final IType itemType = exp.getGamlType();
		final IType cType = itemType.getContentType();
		if (itemType.id() == IType.LIST && cType.id() == IType.LIST) {
			if (exp instanceof ListExpression) {
				final IExpression[] array = ((ListExpression) exp).getElements();
				if (array.length == 0) return Types.NO_TYPE;
				return array[0].getGamlType().getContentType();
			} else if (exp instanceof MapExpression) {
				final IExpression[] array = ((MapExpression) exp).valuesArray();
				if (array.length == 0) return Types.NO_TYPE;
				return array[0].getGamlType().getContentType();
			} else
				return cType.getContentType();
		}
		if (Types.CONTAINER.isAssignableFrom(itemType)) return itemType.getContentType();
		return itemType;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
