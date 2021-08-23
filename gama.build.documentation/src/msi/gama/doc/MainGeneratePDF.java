/*********************************************************************************************
 * 
 *
 * 'MainGenerateWiki.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc;

import msi.gama.doc.util.ConvertToPDF;
import msi.gama.doc.util.GamaStyleGeneration;
import msi.gama.doc.util.PrepareEnv;

public class MainGeneratePDF {

	public static boolean generateGamaStyle = false;
	
	public static void main(String[] args) {
		try {
			System.out.println("GENERATION OF THE PDF DOCUMENTATION");
		
		if (generateGamaStyle) {
			GamaStyleGeneration.generateGamaStyle();
		}

			System.out.print("Preparation of the folders.......................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			System.out.println("Generation of the PDF file .................");
			ConvertToPDF.convert();
			System.out.println("DONE");			
		} catch(Exception e){
			System.out.println("ERROR: Impossible connection to the SVN repository.");
			System.out.println(e);
		}
	}

}
	