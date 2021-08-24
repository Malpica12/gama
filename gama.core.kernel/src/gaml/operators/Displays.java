/*******************************************************************************************************
 *
 * Displays.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.operators;

import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.no_test;
import gama.core.dev.annotations.GamlAnnotations.operator;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IList;
import gama.util.IMap;
import gama.util.tree.GamaNode;
import gaml.types.IType;

/**
 * The Class Displays.
 */
public class Displays {

	/** The Constant HORIZONTAL. */
	public static final String HORIZONTAL = "horizontal";
	
	/** The Constant VERTICAL. */
	public static final String VERTICAL = "vertical";
	
	/** The Constant STACK. */
	public static final String STACK = "stack";

	// @operator (
	// value = IKeyword.LAYOUT,
	// can_be_const = false,
	// doc = @doc (""))
	//
	// public static GamaTree<String> layout(final IScope scope, final GamaNode<String> root) {
	// final GamaTree<String> tree = GamaTree.withRoot(IKeyword.LAYOUT);
	// root.attachTo(tree.getRoot());
	// DEBUG.OUT("Tree: " + tree);
	// return tree;
	// }
	//
	// @operator (
	// value = IKeyword.LAYOUT,
	// can_be_const = false,
	// doc = @doc (""))
	//
	// public static GamaTree<String> layout(final IScope scope, final GamaPair<Object, Integer> pair) {
	// if (pair.key instanceof GamaNode && ((GamaNode<?>) pair.key).getData() instanceof String) { return layout(scope,
	// (GamaNode<String>) pair.key); }
	// throw GamaRuntimeException.error("Layout argument is not recognized", scope);
	// }
	//
	// @operator (
	// value = IKeyword.LAYOUT,
	// can_be_const = false,
	// doc = @doc (""))
	//
	// public static GamaTree<String> layout(final IScope scope, final IMap<Object, Integer> map) {
	// if (map != null) {
	// if (map.size() == 1) {
	// final GamaPair<Object, Integer> pair = (GamaPair<Object, Integer>) map.getPairs().firstValue(scope);
	// if (pair.key instanceof GamaNode && ((GamaNode<?>) pair.key)
	// .getData() instanceof String) { return layout(scope, (GamaNode<String>) pair.key); }
	// } else {
	// return layout(scope, horizontal(scope, map));
	// }
	// }
	// throw GamaRuntimeException.error("Layout argument is not recognized", scope);
	// }

	/**
	 * Horizontal.
	 *
	 * @param scope the scope
	 * @param nodes the nodes
	 * @return the gama node
	 */
	@operator (
			value = HORIZONTAL,
			expected_content_type = IType.FLOAT,
			can_be_const = false)
	@doc ("Creates a horizontal layout node (a sash). Sashes can contain any number (> 1) of other elements: stacks, horizontal or vertical sashes, or display indices. Each element is represented by a pair in the map, where the key is the element and the value its weight within the sash")
	@no_test
	public static GamaNode<String> horizontal(final IScope scope, final IMap<Object, Integer> nodes) {
		return buildSashFromMap(scope, HORIZONTAL, nodes);
	}

	/**
	 * Vertical.
	 *
	 * @param scope the scope
	 * @param nodes the nodes
	 * @return the gama node
	 */
	@operator (
			value = VERTICAL,
			expected_content_type = IType.FLOAT,
			can_be_const = false)
	@doc ("Creates a vertical layout node (a sash). Sashes can contain any number (> 1) of other elements: stacks, horizontal or vertical sashes, or display indices. Each element is represented by a pair in the map, where the key is the element and the value its weight within the sash")
	@no_test
	public static GamaNode<String> vertical(final IScope scope, final IMap<Object, Integer> nodes) {
		return buildSashFromMap(scope, VERTICAL, nodes);
	}

	/**
	 * Stack.
	 *
	 * @param scope the scope
	 * @param nodes the nodes
	 * @return the gama node
	 */
	@operator (
			value = STACK,
			can_be_const = false)
	@doc ("Creates a stack layout node. Stacks can only contain one or several indices of displays (without weight)")
	@no_test
	public static GamaNode<String> stack(final IScope scope, final IList<Integer> nodes) {
		if (nodes == null) { throw GamaRuntimeException.error("Nodes of a stack cannot be nil", scope); }
		if (nodes.isEmpty()) {
			throw GamaRuntimeException.error("At least one display must be defined in the stack", scope);
		}
		final GamaNode<String> node = new GamaNode<>(STACK);
		nodes.forEach(n -> node.addChild(String.valueOf(n)));
		return node;
	}

	/**
	 * Builds the sash from map.
	 *
	 * @param scope the scope
	 * @param orientation the orientation
	 * @param nodes the nodes
	 * @return the gama node
	 */
	@SuppressWarnings ("unchecked")
	private static GamaNode<String> buildSashFromMap(final IScope scope, final String orientation,
			final IMap<Object, Integer> nodes) {
		if (nodes == null) {
			throw GamaRuntimeException.error("Nodes of a " + orientation + " layout cannot be nil", scope);
		}
		if (nodes.size() < 2) {
			throw GamaRuntimeException.error("At least two elements must be defined in this " + orientation + " layout",
					scope);
		}
		final GamaNode<String> node = new GamaNode<>(orientation);
		nodes.forEach((key, value) -> {
			if (key instanceof GamaNode) {
				final GamaNode<String> n = (GamaNode<String>) key;
				n.setWeight(Cast.asInt(scope, value));
				n.attachTo(node);
			} else {
				final Integer index = Cast.asInt(scope, key);
				node.addChild(String.valueOf(index), Cast.asInt(scope, value));
			}
		});
		return node;
	}

}
