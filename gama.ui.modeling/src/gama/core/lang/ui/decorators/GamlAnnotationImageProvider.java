/*********************************************************************************************
 *
 * 'GamlAnnotationImageProvider.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.core.lang.ui.decorators;

import static org.eclipse.xtext.ui.editor.XtextEditor.ERROR_ANNOTATION_TYPE;
import static org.eclipse.xtext.ui.editor.XtextEditor.INFO_ANNOTATION_TYPE;
import static org.eclipse.xtext.ui.editor.XtextEditor.WARNING_ANNOTATION_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.xtext.ui.editor.model.XtextMarkerAnnotationImageProvider;
import org.eclipse.xtext.ui.editor.validation.XtextAnnotation;

import com.google.inject.Inject;

import gama.ui.base.resources.GamaIcon;
import gama.ui.base.resources.GamaIcons;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlAnnotationImageProvider extends XtextMarkerAnnotationImageProvider {

	private static final Map<String, GamaIcon> fixables = new HashMap() {

		{
			put(ERROR_ANNOTATION_TYPE, GamaIcons.create("marker.error2"));
			put(WARNING_ANNOTATION_TYPE, GamaIcons.create("marker.warning2"));
			put(INFO_ANNOTATION_TYPE, GamaIcons.create("marker.info2"));
			put("org.eclipse.ui.workbench.texteditor.task", GamaIcons.create("marker.task2"));
		}
	};
	private static final Map<String, GamaIcon> nonFixables = new HashMap() {

		{
			put(ERROR_ANNOTATION_TYPE, GamaIcons.create("marker.error2"));
			put(WARNING_ANNOTATION_TYPE, GamaIcons.create("marker.warning2"));
			put(INFO_ANNOTATION_TYPE, GamaIcons.create("marker.info2"));
			put("org.eclipse.ui.workbench.texteditor.task", GamaIcons.create("marker.task2"));
		}
	};
	private static final Map<String, GamaIcon> deleted = new HashMap() {

		{
			put(ERROR_ANNOTATION_TYPE, GamaIcons.create("marker.deleted2"));
			put(WARNING_ANNOTATION_TYPE, GamaIcons.create("marker.deleted2"));
			put(INFO_ANNOTATION_TYPE, GamaIcons.create("marker.deleted2"));
			put("org.eclipse.ui.workbench.texteditor.task", GamaIcons.create("marker.deleted2"));
		}
	};

	@Inject
	public GamlAnnotationImageProvider() {}

	@Override
	public Image getManagedImage(final Annotation annotation) {

		// final AnnotationPreference pref;
		GamaIcon result = null;
		if (annotation.isMarkedDeleted()) {
			result = deleted.get(annotation.getType());
		} else {
			if (annotation instanceof MarkerAnnotation) {
				final MarkerAnnotation ma = (MarkerAnnotation) annotation;
				if (ma.isQuickFixableStateSet() && ma.isQuickFixable()) {
					result = fixables.get(annotation.getType());
				} else {
					result = nonFixables.get(annotation.getType());
				}
			} else if (annotation instanceof ProjectionAnnotation) {
				return null;
				// ProjectionAnnotation projection = (ProjectionAnnotation)
				// annotation;
				// if ( projection.isCollapsed() ) {
				// return GamaIcons.create("marker.collapsed2").image();
				// } else {
				// return GamaIcons.create("marker.expanded2").image();
				// }
			} else if (annotation instanceof XtextAnnotation) {
				final XtextAnnotation ma = (XtextAnnotation) annotation;
				if (ma.isQuickFixable()) {
					result = fixables.get(annotation.getType());
				} else {
					result = nonFixables.get(annotation.getType());
				}
			}
		}
		if (result != null) { return result.image(); }
		// DEBUG.LOG("Image not found for type: " +
		// annotation.getType());
		return super.getManagedImage(annotation);
	}

}