/*
 * generated by Xtext
 */
package msi.gama.lang.gaml;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.XtextPackage;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.impl.BinaryGrammarResourceFactoryImpl;

import com.google.inject.Guice;
import com.google.inject.Injector;

import gama.core.lang.GamlRuntimeModule;
import msi.gama.lang.gaml.gaml.GamlPackage;

@SuppressWarnings ("all")
public class GamlStandaloneSetupGenerated implements ISetup {

	@Override
	public Injector createInjectorAndDoEMFRegistration() {
		// register default ePackages
		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("ecore")) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		}
		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xmi")) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		}
		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xtextbin")) {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xtextbin",
					new BinaryGrammarResourceFactoryImpl());
		}
		if (!EPackage.Registry.INSTANCE.containsKey(XtextPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(XtextPackage.eNS_URI, XtextPackage.eINSTANCE);
		}

		Injector injector = createInjector();
		register(injector);
		return injector;
	}

	public Injector createInjector() {
		return Guice.createInjector(new GamlRuntimeModule());
	}

	public void register(final Injector injector) {
		if (!EPackage.Registry.INSTANCE.containsKey("http://www.gama.msi/lang/gaml/Gaml")) {
			EPackage.Registry.INSTANCE.put("http://www.gama.msi/lang/gaml/Gaml", GamlPackage.eINSTANCE);
		}
		IResourceFactory resourceFactory = injector.getInstance(IResourceFactory.class);
		IResourceServiceProvider serviceProvider = injector.getInstance(IResourceServiceProvider.class);

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("gaml", resourceFactory);
		IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put("gaml", serviceProvider);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("experiment", resourceFactory);
		IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put("experiment", serviceProvider);
	}
}
