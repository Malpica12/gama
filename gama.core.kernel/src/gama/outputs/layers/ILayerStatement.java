/*******************************************************************************************************
 *
 * ILayerStatement.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.outputs.layers;

import gama.common.interfaces.IKeyword;
import gama.common.interfaces.IStepable;
import gama.outputs.IDisplayOutput;
import gama.outputs.LayeredDisplayOutput;
import gaml.compilation.ISymbol;
import gaml.expressions.IExpression;

/**
 * The class ILayerStatement. Supports the GAML definition of layers in a display
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface ILayerStatement extends IStepable, ISymbol, Comparable<ILayerStatement> {

	/**
	 * The Enum LayerType.
	 */
	public enum LayerType {

		/** The grid. */
		GRID(IKeyword.GRID),
		
		/** The agents. */
		AGENTS(IKeyword.AGENTS),
		
		/** The grid agents. */
		GRID_AGENTS("grid_agents"),
		
		/** The species. */
		SPECIES(IKeyword.SPECIES),
		
		/** The image. */
		IMAGE(IKeyword.IMAGE),
		
		/** The gis. */
		GIS(IKeyword.GIS),
		
		/** The chart. */
		CHART(IKeyword.CHART),
		
		/** The event. */
		EVENT(IKeyword.EVENT),
		
		/** The graphics. */
		GRAPHICS(IKeyword.GRAPHICS),
		
		/** The overlay. */
		OVERLAY(IKeyword.OVERLAY),
		
		/** The camera. */
		CAMERA(IKeyword.CAMERA),
		
		/** The light. */
		LIGHT("light"),
		
		/** The mesh. */
		MESH(IKeyword.MESH);

		/** The name. */
		private final String name;

		/**
		 * Instantiates a new layer type.
		 *
		 * @param s the s
		 */
		LayerType(final String s) {
			name = s;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	/**
	 * Gets the type.
	 *
	 * @param output the output
	 * @return the type
	 */
	LayerType getType(LayeredDisplayOutput output);

	/**
	 * Sets the display output.
	 *
	 * @param output the new display output
	 */
	void setDisplayOutput(IDisplayOutput output);

	/**
	 * Gets the refresh facet.
	 *
	 * @return the refresh facet
	 */
	IExpression getRefreshFacet();

}