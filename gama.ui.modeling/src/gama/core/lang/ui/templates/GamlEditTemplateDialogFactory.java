/*********************************************************************************************
 *
 * 'GamlEditTemplateDialogFactory.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.core.lang.ui.templates;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.swt.widgets.Shell;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.EditTemplateDialogFactory;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplateResourceProvider;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplatesLanguageConfiguration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * The class GamlEditTemplateDialogFactory.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */

@SuppressWarnings ("deprecation")
public class GamlEditTemplateDialogFactory extends EditTemplateDialogFactory {

	@Inject private Provider<TemplatesLanguageConfiguration> configurationProvider;

	@Inject private ContextTypeRegistry contextTypeRegistry;

	@Inject private TemplateResourceProvider resourceProvider;

	@Inject @Named (Constants.LANGUAGE_NAME) private String languageName;

	public GamlEditTemplateDialog createDialog(final TemplatePersistenceData template, final boolean edit,
			final Shell shell) {
		final GamlEditTemplateDialog dialog = new GamlEditTemplateDialog(shell, template, edit, contextTypeRegistry,
				configurationProvider.get(), resourceProvider, languageName);
		return dialog;
	}

}
