/*******************************************************************************************************
 *
 * OperatorsReferenceMenu.java, in gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.modeling.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import gama.common.preferences.GamaPreferences;
import gama.ui.base.resources.GamaIcons;
import gama.ui.modeling.templates.GamlTemplateFactory;
import gama.util.IMap;
import gaml.descriptions.OperatorProto;
import gaml.expressions.IExpressionCompiler;
import gaml.types.Signature;

/**
 * The class EditToolbarTemplateMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class OperatorsReferenceMenu extends GamlReferenceMenu {

	/** The by name. */
	public static Boolean byName = null;

	@Override
	protected void fillMenu() {
		if (byName == null) {
			byName = GamaPreferences.Modeling.OPERATORS_MENU_SORT.getValue().equals("Name");
		}
		// final Menu sub = sub("Sort by...");
		// sep();
		// check(sub, "Name", byName, new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent event) {
		// byName = true;
		// reset();
		// }
		// });
		// check(sub, "Category", !byName, new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent event) {
		// byName = false;
		// reset();
		// }
		// });
		if (byName) {
			fillMenuByName();
		} else {
			fillMenuByCategory();
		}
	}

	/**
	 * Fill menu by name.
	 */
	protected void fillMenuByName() {
		final IMap<String, IMap<Signature, OperatorProto>> operators = IExpressionCompiler.OPERATORS;
		final List<String> nn = new ArrayList(operators.keySet());
		Collections.sort(nn, IGNORE_CASE);
		for (final String name : nn) {
			final List<OperatorProto> protos = new ArrayList<>();
			for (final Signature sig : operators.get(name).keySet()) {
				final OperatorProto proto = operators.get(name).get(sig);
				if (proto.getDeprecated() == null) {
					protos.add(proto);
				}
			}
			if (protos.isEmpty()) {
				continue;
			}
			final Menu name_menu = sub(name);
			for (final OperatorProto proto : protos) {
				final Template t = GamlTemplateFactory.from(proto);
				final MenuItem item = action(name_menu,
						"(" + proto.signature.asPattern(false) + ") -> " + proto.returnType.serialize(true),
						new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent event) {
								applyTemplate(t);
							}
						});
				item.setToolTipText(t.getDescription());
			}
		}
	}

	/**
	 * Fill menu by category.
	 */
	protected void fillMenuByCategory() {
		final IMap<String, IMap<Signature, OperatorProto>> operators = IExpressionCompiler.OPERATORS;
		final Map<String, Map<String, Map<OperatorProto, Template>>> categories = new LinkedHashMap();
		final List<String> nn = new ArrayList(operators.keySet());
		Collections.sort(nn, IGNORE_CASE);
		for (final String name : nn) {
			final Map<Signature, OperatorProto> ops = operators.get(name);
			for (final Signature sig : ops.keySet()) {
				final OperatorProto proto = ops.get(sig);
				if (proto.getDeprecated() != null) {
					continue;
				}
				final String category = proto.getCategory().replace("-related", "");
				Map<String, Map<OperatorProto, Template>> names = categories.get(category);
				if (names == null) {
					names = new LinkedHashMap();
					categories.put(category, names);
				}
				Map<OperatorProto, Template> templates = names.get(name);
				if (templates == null) {
					templates = new LinkedHashMap();
					names.put(name, templates);
				}
				templates.put(proto, GamlTemplateFactory.from(proto));
			}
		}
		final List<String> cc = new ArrayList(categories.keySet());
		Collections.sort(cc, IGNORE_CASE);
		for (final String category : cc) {
			final Menu category_menu = sub(category);
			final List<String> nn2 = new ArrayList(categories.get(category).keySet());
			Collections.sort(nn2, IGNORE_CASE);
			for (final String name : nn2) {
				final List<OperatorProto> protos = new ArrayList(categories.get(category).get(name).keySet());
				//
				final Menu name_menu = sub(category_menu, name);
				for (final OperatorProto proto : protos) {
					final Template t = categories.get(category).get(name).get(proto);
					final MenuItem item = action(name_menu,
							"(" + proto.signature.asPattern(false) + ") -> " + proto.returnType.serialize(true),
							new SelectionAdapter() {

								@Override
								public void widgetSelected(final SelectionEvent event) {
									applyTemplate(t);
								}
							});
					item.setToolTipText(t.getDescription());
				}

			}
		}

	}

	@Override
	protected void openView() {}

	/**
	 * @see gama.ui.modeling.reference.GamlReferenceMenu#getImage()
	 */
	@Override
	protected Image getImage() {
		return GamaIcons.create("reference.operators").image();
	}

	/**
	 * @see gama.ui.modeling.reference.GamlReferenceMenu#getTitle()
	 */
	@Override
	protected String getTitle() {
		return "Operators";
	}

}