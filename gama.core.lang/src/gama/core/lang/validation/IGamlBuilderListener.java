/*********************************************************************************************
 *
 * 'IGamlBuilderListener.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.core.lang.validation;

import gaml.descriptions.IDescription;
import gaml.descriptions.ValidationContext;

/**
 * The class IGamlBuilder.
 * 
 * @author drogoul
 * @since 2 mars 2012
 * 
 */
public interface IGamlBuilderListener {

	void validationEnded(final Iterable<? extends IDescription> experiments, final ValidationContext status);
}
