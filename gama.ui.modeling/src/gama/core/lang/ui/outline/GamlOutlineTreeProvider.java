/*******************************************************************************************************
 *
 * GamlOutlineTreeProvider.java, in gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.core.lang.ui.outline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.BackgroundOutlineTreeProvider;

import com.google.inject.Inject;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.lang.EGaml;
import gama.core.lang.gaml.Block;
import gama.core.lang.gaml.ExperimentFileStructure;
import gama.core.lang.gaml.HeadlessExperiment;
import gama.core.lang.gaml.Model;
import gama.core.lang.gaml.S_Action;
import gama.core.lang.gaml.S_Definition;
import gama.core.lang.gaml.S_Experiment;
import gama.core.lang.gaml.S_Global;
import gama.core.lang.gaml.S_Species;
import gama.core.lang.gaml.Statement;
import gama.core.lang.gaml.util.GamlSwitch;
import gama.core.lang.ui.labeling.GamlLabelProvider;
import gaml.descriptions.SymbolProto;
import gaml.factories.DescriptionFactory;

/**
 * customization of the default outline structure.
 */
public class GamlOutlineTreeProvider extends BackgroundOutlineTreeProvider {

	/** The provider. */
	@Inject private GamlLabelProvider provider;

	/** The Constant FOUND. */
	final static Object FOUND = new Object();

	@Override
	public void createChildren(final IOutlineNode parentNode, final EObject stm) {
		if (stm != null && parentNode.hasChildren()) {
			new GamlSwitch<Object>() {

				@Override
				public Object caseModel(final Model stm) {

					Block block = stm.getBlock();
					if (block != null) {
						for (final Statement s : EGaml.getInstance().getStatementsOf(block)) {
							if (s instanceof S_Global) {
								block = s.getBlock();
								if (block != null) {
									ownCreateChildren(parentNode, block);
								}
							} else {
								createNode(parentNode, s);
							}
						}
					}
					return FOUND;
				}

				@Override
				public Object caseExperimentFileStructure(final ExperimentFileStructure stm) {
					return caseHeadlessExperiment(stm.getExp());
				}

				@Override
				public Object caseS_Experiment(final S_Experiment stm) {
					ownCreateChildren(parentNode, stm.getBlock());
					return FOUND;
				}

				@Override
				public Object caseHeadlessExperiment(final HeadlessExperiment stm) {
					ownCreateChildren(parentNode, stm.getBlock());
					return FOUND;
				}

				@Override
				public Object caseS_Species(final S_Species stm) {
					ownCreateChildren(parentNode, stm.getBlock());
					return FOUND;
				}

			}.doSwitch(stm);
		}
	}

	/**
	 * Own create children.
	 *
	 * @param parentNode the parent node
	 * @param block the block
	 */
	protected void ownCreateChildren(final IOutlineNode parentNode, final Block block) {

		IOutlineNode attributesNode = null;
		IOutlineNode parametersNode = null;
		IOutlineNode actionsNode = null;
		if (block != null) {
			for (final Statement s : EGaml.getInstance().getStatementsOf(block)) {
				if (isAttribute(s)) {
					if (attributesNode == null) {
						attributesNode = new AbstractOutlineNode(parentNode, provider.convertToImage("_attributes.png"),
								"Attributes", false) {};
					}
					createNode(attributesNode, s);
				} else if (IKeyword.PARAMETER.equals(s.getKey())) {
					if (parametersNode == null) {
						parametersNode = new AbstractOutlineNode(parentNode, provider.convertToImage("_parameter.png"),
								"Parameters", false) {};
					}
					createNode(parametersNode, s);
				} else if (isAction(s)) {
					if (actionsNode == null) {
						actionsNode = new AbstractOutlineNode(parentNode, provider.convertToImage("_action.png"),
								"Actions", false) {};
					}
					createNode(actionsNode, s);

				}

				else {
					createNode(parentNode, s);
				}
			}
		}
	}

	/**
	 * Checks if is attribute.
	 *
	 * @param s the s
	 * @return true, if is attribute
	 */
	public static boolean isAttribute(final Statement s) {
		if (!(s instanceof S_Definition)) { return false; }
		final String key = EGaml.getInstance().getKeyOf(s);
		if (IKeyword.ACTION.equals(key)) { return false; }
		// if (s.getBlock() != null && s.getBlock().getFunction() == null) { return false; }
		final SymbolProto p = DescriptionFactory.getStatementProto(key, null);
		if (p != null && p.getKind() == ISymbolKind.BATCH_METHOD) { return false; }
		return true;
	}

	/**
	 * Checks if is action.
	 *
	 * @param s the s
	 * @return true, if is action
	 */
	public static boolean isAction(final Statement s) {
		if (!(s instanceof S_Definition)) { return false; }
		if (s instanceof S_Action) { return true; }
		final String key = EGaml.getInstance().getKeyOf(s);
		final SymbolProto p = DescriptionFactory.getStatementProto(key, null);
		if (p != null && p.isTopLevel()) { return false; }
		if (s.getKey() == null) { return true; }
		return false;
	}

	@Override
	protected Object getText(final Object modelElement) {
		if (modelElement instanceof S_Global) { return null; }
		return super.getText(modelElement);
	}

	@Override
	protected boolean isLeaf(final EObject s) {
		if (s instanceof S_Experiment) {
			return ((S_Experiment) s).getBlock() == null || ((S_Experiment) s).getBlock().getStatements().isEmpty();
		} else if (s instanceof S_Species) {
			return ((S_Species) s).getBlock() == null || ((S_Species) s).getBlock().getStatements().isEmpty();
		} else if (s instanceof Model) { return ((Model) s).getBlock() == null; }
		return true;
	}
}