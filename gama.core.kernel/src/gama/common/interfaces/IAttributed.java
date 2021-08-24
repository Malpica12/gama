/*******************************************************************************************************
 *
 * IAttributed.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.common.interfaces;

import java.util.Map;

/**
 * Represents objects that are provided with attributes (collection of string-value pairs).
 *
 * @author drogoul
 */
public interface IAttributed {

	

	/**
	 * Allows to retrieve the attributes of the object as a Map.
	 *
	 * @return a map containing the attributes or null if no attributes are defined
	 */
	// Map<String, Object> getAttributes();

	/**
	 * Allows to retrieve the attributes of the object as a Map. If the object has no attributes, should return an empty
	 * map than can be filled and modified
	 *
	 * @return a map containing the attributes or an empty map if no attributes are defined
	 */
	Map<String, Object> getOrCreateAttributes();

	/**
	 * Allows to retrieve the value stored at key "key".
	 *
	 * @param key the key
	 * @return the value stored at key "key". Returns null if no such key exists. However, please note that null is a
	 *         valid value, which means that receiving null when calling this method does not necessarily mean that the
	 *         key is absent. Use hasAttribute(Object key) to verify the presence of a key
	 */
	Object getAttribute(String key);

	/**
	 * Allows to set the value stored at key "key". A new entry is created when "key" is not already present, otherwise
	 * the previous occurrence is replaced.
	 *
	 * @param key the key
	 * @param value the value
	 */

	void setAttribute(String key, Object value);

	/**
	 * Answers whether or not this object has any value set at key "key".
	 *
	 * @param key the key
	 * @return true if the object has such an attribute, false otherwise
	 */
	boolean hasAttribute(String key);

	/**
	 * Allows to visit the attributes like a map. Returns true if all the attributes have been visited, false otherwise.
	 *
	 * @param visitor the visitor
	 */

	void forEachAttribute(BiConsumerWithPruning<String, Object> visitor);

	/**
	 * Copy all the attributes of the other instance of IAttributed.
	 *
	 * @param source the source
	 */

	default void copyAttributesOf(final IAttributed source) {
		if (source != null) {
			source.forEachAttribute((k, v) -> {
				setAttribute(k, v);
				return true;
			});
		}

	}

}
