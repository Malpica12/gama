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

import msi.gama.doc.transform.XmlToWiki;
import msi.gama.doc.util.CheckConcepts;
import msi.gama.doc.util.GenerateCategoryXML;
import msi.gama.doc.util.PrepareEnv;
import msi.gama.doc.util.UnifyDoc;

public class MainGenerateWiki {

	public static void main(final String[] args) { 
		try {
			// build the file keywords.xml
			GenerateCategoryXML.GenerateKeywordsXML();

			// generate the wiki documentation
			System.out.println("GENERATION OF THE WIKI DOCUMENTATION FROM JAVA CODE");
			System.out.println("Please notice that the docGAMA.xml files should have been previously generated..");
			System.out.print("Preparation of the folders................");
			PrepareEnv.prepareDocumentation();
			System.out.println("DONE");

			System.out.print("Merge all the docGAMA.xml files................");
			UnifyDoc.unify((args.length > 0) ? (args[0].equals("-online") ? false : true) : true);
			System.out.println("DONE");

			System.out.print(
					"Transform the docGAMA.xml file into Wiki Files (md) and create/update them in the gama.wiki folder................");
			XmlToWiki.createAllWikis();
			XmlToWiki.createExtentionsWiki();
			System.out.println("DONE");

			// check the concept used, print a report and write it in the file
			// "website generation"
			CheckConcepts.DoCheckConcepts();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
