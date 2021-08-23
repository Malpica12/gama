/*********************************************************************************************
 *
 * 'Operator.java, in plugin msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.build.processor.doc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gama.build.processor.Constants;
import gama.core.dev.documentation.utils.XMLElements;

public class Operator implements IElement {

	Document doc;

	String category;
	String[] concepts;
	String name;
	// Operands operands;
	List<Operands> combiIO;	
//	String documentation;
	Documentation documentation;
	boolean has_example = false;

	public Operator(final Document _doc) {
		doc = _doc;
		combiIO = new ArrayList<>();
		documentation = new Documentation(_doc);
//		operands = new Operands(_doc);
	}

	public Operator(final Document _doc, final String _category, final String[] _concepts, final String _name) {
		this(_doc);
		category = _category;
		name = _name;
		concepts = _concepts;
	}

	public Operator(final Document _doc, final String _category, final String[] _concepts, final String _name,
			final String _documentation) {
		this(_doc);
		category = _category;
		name = _name;
		documentation = new Documentation(doc,_documentation);
		concepts = _concepts;
	}

	public void setDocumentation(final String d) {
		documentation.setResult(d);
	}
	
	public void addOperands(Operands ops) {
		combiIO.add(ops);
	}	
	

	public void addSeeAlso(String see) {
		documentation.addSee(see);
	}
	
	public void addUsage(String descUsage, org.w3c.dom.Element exElt) {
		if(exElt != null) {
			has_example = true;
		}
		documentation.addUsage(descUsage, exElt);
	}	
	
	public void addUsage(String descUsage) {
		this.addUsage(descUsage, null);
	}

	
	@Override
	public Element getElementDOM() {
		// TODO to finish
		final org.w3c.dom.Element eltOp = doc.createElement(XMLElements.OPERATOR);
		// eltOp.setAttribute(XMLElements.ATT_OP_CATEGORY, category);
		eltOp.setAttribute(XMLElements.ATT_OP_ID, name);
		eltOp.setAttribute(XMLElements.ATT_OP_NAME, name);
		eltOp.setAttribute(XMLElements.ATT_ALPHABET_ORDER, Constants.getAlphabetOrder(name));
		if(has_example) {
			eltOp.setAttribute("HAS_TEST", "true");			
		}
			
		// Categories
		final org.w3c.dom.Element categoriesElt = doc.createElement(XMLElements.OPERATOR_CATEGORIES);
		final org.w3c.dom.Element catElt = doc.createElement(XMLElements.CATEGORY);
		catElt.setAttribute(XMLElements.ATT_CAT_ID, category);
		categoriesElt.appendChild(catElt);
		eltOp.appendChild(categoriesElt);
		
		// Concepts
		final org.w3c.dom.Element conceptsElt = doc.createElement(XMLElements.CONCEPTS);
		for (final String conceptName : concepts) {
			final org.w3c.dom.Element conceptElt = doc.createElement(XMLElements.CONCEPT);
			conceptElt.setAttribute(XMLElements.ATT_CAT_ID, conceptName);
			conceptsElt.appendChild(conceptElt);
		}
		eltOp.appendChild(conceptsElt);

		// Combinaison IO
		final org.w3c.dom.Element combiElt = doc.createElement(XMLElements.COMBINAISON_IO);
		for(Operands ops : combiIO) {
			combiElt.appendChild(ops.getElementDOM());
		}
		eltOp.appendChild(combiElt);

		// Documentation
		eltOp.appendChild(documentation.getElementDOM());

		return eltOp;
	}


}
