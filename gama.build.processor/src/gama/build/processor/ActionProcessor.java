/*******************************************************************************************************
 *
 * ActionProcessor.java, in gama.build.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.build.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import gama.core.dev.annotations.GamlAnnotations.action;
import gama.core.dev.annotations.GamlAnnotations.arg;

/**
 * The Class ActionProcessor.
 */
public class ActionProcessor extends ElementProcessor<action> {

	/** The reserved facets. */
	private static Set<String> RESERVED_FACETS = new HashSet<>(Arrays.asList("name", "keyword", "returns"));
	
	/** The temp. */
	Set<String> temp = new HashSet<>();

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e,
			final action action) {
		final String method = e.getSimpleName().toString();
		final String clazz = rawNameOf(context, e.getEnclosingElement().asType());
		final String ret = checkPrim(getReturnType(context, (ExecutableElement) e));
		sb.append(in).append("_action(").append("(s,a,t,v)->").append(!"void".equals(ret) ? "" : "{").append("((")
				.append(clazz).append(") t).").append(method).append("(s)")
				.append("void".equals(ret) ? ";return null;}," : ",").append("desc(PRIM,");
		buildArgs(context, e, action.args(), sb).append(",NAME,").append(toJavaString(action.name()))
				.append(",TYPE,Ti(").append(toClassObject(ret)).append("),VIRTUAL,")
				.append(toJavaString(String.valueOf(action.virtual()))).append(')').append(',')
				.append(toClassObject(clazz)).append(".getMethod(").append(toJavaString(method)).append(',')
				.append(toClassObject(ISCOPE)).append("));");
	}

	@Override
	protected Class<action> getAnnotationClass() {
		return action.class;
	}

	/**
	 * Builds the args.
	 *
	 * @param context the context
	 * @param e the e
	 * @param args the args
	 * @param sb the sb
	 * @return the string builder
	 */
	private final StringBuilder buildArgs(final ProcessorContext context, final Element e, final arg[] args,
			final StringBuilder sb) {
		sb.append("new Children(");
		// TODO Argument types not taken into account when declaring them

		for (int i = 0; i < args.length; i++) {
			final arg arg = args[i];
			if (i > 0) { sb.append(','); }
			final String argName = arg.name();
			if (RESERVED_FACETS.contains(argName)) {
				context.emitWarning("Argument '" + argName
						+ "' prevents this action to be called using facets (e.g. 'do action arg1: val1 arg2: val2;'). Consider renaming it to a non-reserved facet keyword",
						e);
			}
			if (temp.contains(argName)) {
				context.emitError("Argument '" + argName + "' is declared twice", e);
			} else {
				temp.add(argName);
			}
			verifyDoc(context, e, "argument " + arg.name(), arg);
			sb.append("desc(ARG,NAME,").append(toJavaString(argName)).append(",TYPE,")
					.append(toJavaString(String.valueOf(arg.type()))).append(",\"optional\",")
					.append(toJavaString(String.valueOf(arg.optional()))).append(')');
		}
		sb.append(")");
		temp.clear();
		return sb;

	}

	/**
	 * Gets the return type.
	 *
	 * @param context the context
	 * @param ex the ex
	 * @return the return type
	 */
	private String getReturnType(final ProcessorContext context, final ExecutableElement ex) {
		final TypeMirror tm = ex.getReturnType();
		if (TypeKind.VOID.equals(tm.getKind())) return "void";
		return rawNameOf(context, tm);
	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

	/**
	 * By construction, action can only annotate methods so no need to verify this
	 */
	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result = assertNotVoid(context, false, (ExecutableElement) e);
		result &= assertArgumentsSize(context, true, (ExecutableElement) e, 1);
		result &= assertContainsScope(context, true, (ExecutableElement) e);
		result &= assertClassIsAgentOrSkill(context, true, (TypeElement) e.getEnclosingElement());
		return result;
	}

}
