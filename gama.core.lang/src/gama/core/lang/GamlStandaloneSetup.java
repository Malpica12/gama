/*********************************************************************************************
 *
 * 'GamlStandaloneSetup.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/

package gama.core.lang;

import com.google.inject.Injector;

import msi.gama.lang.gaml.GamlStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages without equinox extension registry
 */
public class GamlStandaloneSetup extends GamlStandaloneSetupGenerated {

	public static Injector doSetup() {
		return new GamlStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
	}
}