/*********************************************************************************************
 *
 * 'Category.java, in plugin msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.build.processor.doc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gama.core.dev.documentation.utils.XMLElements;

public class Category implements IElement {

	Document doc;
	String idCategory;
	
	public Category(Document _doc, String id){
		doc = _doc;
		idCategory = id;
	}
	
	@Override
	public Element getElementDOM() {
		Element eltCat = doc.createElement(XMLElements.CATEGORY);
		eltCat.setAttribute("id", idCategory);
		return eltCat;
	}

}
