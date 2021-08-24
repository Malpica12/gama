/*********************************************************************************************
 *
 * 'GamlTemplateFactory.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.modeling.templates;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.templates.Template;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import gama.common.interfaces.INamed;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.usage;
import gama.core.dev.annotations.ISymbolKind;
import gaml.compilation.AbstractGamlAdditions;
import gaml.compilation.kernel.GamaSkillRegistry;
import gaml.descriptions.AbstractProto;
import gaml.descriptions.IDescription;
import gaml.descriptions.OperatorProto;
import gaml.descriptions.StatementDescription;
import gaml.descriptions.TypeDescription;
import gaml.operators.Strings;
import gaml.types.Types;

/**
 * The class GamlTemplateFactory.
 *
 * @author drogoul
 * @since 12 déc. 2014
 *
 */

@SuppressWarnings ("deprecation")
public class GamlTemplateFactory {

	public static String getContextName() {
		return "Model";
	}

	public static String getContextId() {
		return "gama.core.lang.Gaml.Model";
	}

	public static TemplatePersistenceData from(final usage u, final AbstractProto sp) {
		boolean isExample = false;
		String name = u.name();
		boolean emptyName = name.isEmpty();
		String pattern = u.pattern();
		if (pattern.isEmpty()) {
			for (final example e : u.examples()) {
				if (emptyName) {
					name = e.value();
					emptyName = false;
				}
				if (!e.isPattern()) { isExample = true; }
				// if ( e.isPattern() ) {
				pattern += Strings.LN + e.value();
				// }
			}
		}
		if (pattern.isEmpty()) return null;
		pattern += Strings.LN;
		String[] path = u.path();
		if (path.length == 0) { path = new String[] { StringUtils.capitalize(sp.getName()) }; }
		String menuPath = "";
		for (final String p : path) {
			menuPath += p + ".";
		}
		String menu = u.menu();
		if (usage.NULL.equals(menu)) { menu = ISymbolKind.TEMPLATE_MENU[sp.getKind()]; }
		String desc = u.value();
		if (usage.NULL.equals(desc)) {
			// Trying to build something that makes sense..
			desc = menu + " " + name;
			desc += Strings.LN;
			final String doc = sp.getDocumentation();
			int index = doc.indexOf(". ");
			if (index == -1) { index = doc.length(); }
			desc += doc.substring(0, Math.min(index, 150)) + " [...]";
		}
		menuPath = menu + "." + menuPath.substring(0, menuPath.length() - 1);
		if (isExample) { menuPath = "Examples." + menuPath; }
		final Template template = new Template(name, desc, getContextId(), pattern, true);
		return new TemplatePersistenceData(template, true, menuPath);

	}

	static String beginComment = "/**" + Strings.LN;
	static String endComment = "*/" + Strings.LN;
	static String commentLine = Strings.LN + "* " + Strings.TAB + Strings.TAB;
	static String inheritedAttributes = "* Inherited attributes:";
	static String inheritedActions = "* Inherited actions:";
	static String availableBehaviors = "* Available behaviors:";

	private static String body(final String body) {
		final StringBuilder sb = new StringBuilder(200);
		sb.append(" {").append(Strings.LN);
		sb.append(body);
		sb.append(Strings.LN).append("${cursor}");
		sb.append(Strings.LN).append("}").append(Strings.LN);
		return sb.toString();
	}

	private static void dump(final String title, final Iterable<? extends INamed> descs, final StringBuilder sb) {
		if (!Iterables.isEmpty(descs)) {
			final List<INamed> named = Lists.newArrayList(descs);
			Collections.sort(named, INamed.COMPARATOR);
			sb.append(title);
			for (final INamed sd : named) {
				sb.append(commentLine).append(sd.serialize(true));
			}
			sb.append(Strings.LN);
		}
	}

	public static Template speciesWithSkill(final String skill) {
		final StringBuilder comment = new StringBuilder(200);
		comment.append(beginComment);
		dump(inheritedAttributes, GamaSkillRegistry.INSTANCE.getVariablesForSkill(skill), comment);
		dump(inheritedActions, GamaSkillRegistry.INSTANCE.getActionsForSkill(skill), comment);
		comment.append(endComment);
		return new Template("A species with the skill " + skill,
				"Defines a species that implements the skill named " + skill, getContextId(),
				"species ${species_name} skills: [" + skill + "]" + body(comment.toString()), true);
	}

	public static Template attributeWithType(final String type) {
		return new Template("An attribute of type " + type, "Defines an attribute of type " + type, getContextId(),
				type + " " + Types.get(type).asPattern() + " <- ${initial_value};", true);
	}

	public static Template speciesWithControl(final String skill) {
		// Collection<SymbolProto> controls =
		// AbstractGamlAdditions.getStatementsForSkill(skill);
		final StringBuilder comment = new StringBuilder(200);
		comment.append(beginComment);
		dump(inheritedAttributes, GamaSkillRegistry.INSTANCE.getVariablesForSkill(skill), comment);
		dump(inheritedActions, GamaSkillRegistry.INSTANCE.getActionsForSkill(skill), comment);
		dump(availableBehaviors, AbstractGamlAdditions.getStatementsForSkill(skill), comment);
		comment.append(endComment);
		return new Template("A species with the control " + skill,
				"Defines a species that implements the control named " + skill, getContextId(),
				"species ${species_name} control: " + skill + body(comment.toString()), true);
	}

	public static Template speciesWithParent(final TypeDescription species) {
		final String name = species.getName();
		final StringBuilder comment = new StringBuilder(200);
		comment.append(beginComment);
		dump(inheritedAttributes, species.getAttributes(), comment);
		dump(inheritedActions, species.getActions(), comment);
		comment.append(endComment);
		return new Template("A species with the parent " + name,
				"Defines a species that implements the control named " + name, getContextId(),
				"species ${species_name} parent: " + name + body(comment.toString()), true);
	}

	public static Template callToAction(final StatementDescription sd) {
		final String name = sd.getName();
		final Iterable<IDescription> args = sd.getFormalArgs();
		final StringBuilder sb = new StringBuilder(100);
		sb.append("(");
		for (final IDescription arg : args) {
			sb.append(arg.getName()).append(": ").append("${the_").append(arg.getName()).append("}, ");
		}
		final int length = sb.length();
		if (length > 0) { sb.setLength(length - 2); }
		sb.append(")");
		return new Template("A call to action " + name, "A call to action " + name + " will all its arguments",
				getContextId(), "do " + name + sb.toString() + ";" + Strings.LN, true);
	}

	/**
	 * @param proto
	 * @return
	 */
	public static Template from(final OperatorProto proto) {
		String description = proto.getMainDoc();
		if (description == null) { description = "Template for using operator " + proto.getName(); }
		return new Template("Operator " + proto.getName(), description, getContextId(), proto.getPattern(true), true);
	}

}
