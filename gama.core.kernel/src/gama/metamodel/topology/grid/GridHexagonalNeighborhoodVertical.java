/*******************************************************************************************************
 *
 * GridHexagonalNeighborhoodVertical.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.metamodel.topology.grid;

import java.util.HashSet;
import java.util.Set;

/**
 * The Class GridHexagonalNeighborhoodVertical.
 */
public class GridHexagonalNeighborhoodVertical extends GridHexagonalNeighborhood {

	/**
	 * Instantiates a new grid hexagonal neighborhood vertical.
	 *
	 * @param matrix the matrix
	 */
	GridHexagonalNeighborhoodVertical(final GamaSpatialMatrix matrix) {
		super(matrix);
	}

	@Override
	public Set<Integer> getNeighborsAtRadius1(final int placeIndex, final int xSize, final int ySize,
			final boolean isTorus) {
		final int y = placeIndex / xSize;
		final int x = placeIndex - y * xSize;
		final Set<Integer> neigh = new HashSet<>();
		int id = getIndexAt(x, y - 1, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		id = getIndexAt(x, y + 1, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		id = getIndexAt(x - 1, y, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		id = getIndexAt(x + 1, y, xSize, ySize, isTorus);
		if (id != -1) {
			neigh.add(id);
		}
		if (y % 2 != 0) {
			id = getIndexAt(x + 1, y - 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
			id = getIndexAt(x + 1, y + 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
		} else {
			id = getIndexAt(x - 1, y - 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
			id = getIndexAt(x - 1, y + 1, xSize, ySize, isTorus);
			if (id != -1) {
				neigh.add(id);
			}
		}
		return neigh;
	}

}