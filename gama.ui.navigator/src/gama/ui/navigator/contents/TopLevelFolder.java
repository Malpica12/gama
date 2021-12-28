/*******************************************************************************************************
 *
 * TopLevelFolder.java, in gama.ui.navigator, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.navigator.contents;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import gama.core.application.bundles.GamaBundleLoader;
import gama.ui.base.resources.GamaColors.GamaUIColor;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.resources.IGamaIcons;
import one.util.streamex.StreamEx;

// TODO: Auto-generated Javadoc
/**
 * Class TopLevelFolder.
 *
 * @author drogoul
 * @since 30 déc. 2015
 *
 */
public class TopLevelFolder extends VirtualContent<NavigatorRoot> implements IGamaIcons, IGamaColors {

	/**
	 * The Enum Location.
	 */
	public enum Location {

		/** The Core models. */
		CoreModels,
		/** The Plugins. */
		Plugins,
		/** The Other. */
		Other,
		/** The Unknown. */
		Unknown,
		/** The Tests. */
		Tests
	}

	/** The children. */
	WrappedProject[] children;

	/** The status icon. */
	final Image icon, statusIcon;

	/** The nature. */
	final String statusMessage, nature;

	/** The status color. */
	final GamaUIColor statusColor;

	/** The location. */
	final Location location;

	/**
	 * Instantiates a new top level folder.
	 *
	 * @param root
	 *            the root
	 * @param name
	 *            the name
	 * @param iconName
	 *            the icon name
	 * @param statusIconName
	 *            the status icon name
	 * @param statusMessage
	 *            the status message
	 * @param statusColor
	 *            the status color
	 * @param nature
	 *            the nature
	 * @param location
	 *            the location
	 */
	public TopLevelFolder(final NavigatorRoot root, final String name, final String iconName,
			final String statusIconName, final String statusMessage, final GamaUIColor statusColor, final String nature,
			final Location location) {
		super(root, name);
		this.statusColor = statusColor;
		this.statusMessage = statusMessage;
		this.nature = nature;
		this.location = location;
		icon = GamaIcons.create(iconName).image();
		statusIcon = GamaIcons.create(statusIconName).image();
		initializeChildren();
	}

	/**
	 * Initialize children.
	 */
	public void initializeChildren() {
		children = StreamEx.of(ResourcesPlugin.getWorkspace().getRoot().getProjects()).filter(this::privateAccepts)
				.map(p -> (WrappedProject) getManager().wrap(this, p)).toArray(WrappedProject.class);
	}

	/**
	 * Checks for children.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasChildren() {
		return children.length > 0;
	}

	// @Override
	// public Font getFont() {
	// return GamaFonts.getNavigHeaderFont();
	// }

	/**
	 * Gets the navigator children.
	 *
	 * @return the navigator children
	 */
	@Override
	public Object[] getNavigatorChildren() { return children; }

	/**
	 * Find max problem severity.
	 *
	 * @return the int
	 */
	@Override
	public int findMaxProblemSeverity() {
		var severity = NO_PROBLEM;
		for (final WrappedProject p : children) {
			final var s = p.findMaxProblemSeverity();
			if (s > severity) { severity = s; }
			if (severity == IMarker.SEVERITY_ERROR) { break; }
		}
		return severity;
	}

	/**
	 * Private accepts.
	 *
	 * @param project
	 *            the project
	 * @return true, if successful
	 */
	public final boolean privateAccepts(final IProject project) {
		if (project == null || !project.exists()) return false;
		// TODO This one is clearly a hack. Should be replaced by a proper way
		// to track persistently the closed projects
		if (!project.isOpen()) return estimateLocation(project.getLocation()) == location;
		try {
			return accepts(project.getDescription());
		} catch (final CoreException e) {
			return false;
		}
	}

	/**
	 * Estimate location.
	 *
	 * @param location
	 *            the location
	 * @return the location
	 */
	protected Location estimateLocation(final IPath location) {
		try {
			final var old_url = new URL("platform:/plugin/" + GamaBundleLoader.CORE_MODELS.getSymbolicName() + "/");
			final var new_url = FileLocator.toFileURL(old_url);
			// windows URL formating
			final var resolvedURI = new URI(new_url.getProtocol(), new_url.getPath(), null).normalize();
			final var urlRep = resolvedURI.toURL();
			final var osString = location.toOSString();
			final var isTest = osString.contains(GamaBundleLoader.REGULAR_TESTS_LAYOUT);
			if (!isTest && osString.startsWith(urlRep.getPath())) return Location.CoreModels;
			if (osString
					.startsWith(urlRep.getPath().replace(GamaBundleLoader.CORE_MODELS.getSymbolicName() + "/", ""))) {
				if (isTest) return Location.Tests;
				return Location.Plugins;
			}
			return Location.Other;
		} catch (final IOException | URISyntaxException e) {
			e.printStackTrace();
			return Location.Unknown;
		}
	}

	/**
	 * Accepts.
	 *
	 * @param desc
	 *            the desc
	 * @return true, if successful
	 */
	public final boolean accepts(final IProjectDescription desc) {
		if (nature != null) return desc.hasNature(nature);
		return desc.getNatureIds().length < 3;
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	@Override
	public final Image getImage() { return icon; }

	/**
	 * Gets the status image.
	 *
	 * @return the status image
	 */
	@Override
	public Image getStatusImage() { return statusIcon; }

	/**
	 * Gets the status message.
	 *
	 * @return the status message
	 */
	@Override
	public String getStatusMessage() { return statusMessage; }

	/**
	 * Gets the status color.
	 *
	 * @return the status color
	 */
	@Override
	public GamaUIColor getStatusColor() { return statusColor; }
	//
	// @Override
	// public Color getColor() {
	// return ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.GRAY_LABEL.color();
	// }

	/**
 * Gets the suffix.
 *
 * @param sb the sb
 * @return the suffix
 */
@Override
	public void getSuffix(final StringBuilder sb) {
		final var projectCount = children.length;
		sb.append(projectCount).append(" project");
		if (projectCount > 1) { sb.append("s"); }
	}

	/**
	 * Gets the overlay.
	 *
	 * @return the overlay
	 */
	@Override
	public ImageDescriptor getOverlay() { return DESCRIPTORS.get(findMaxProblemSeverity()); }

	/**
	 * Gets the top level folder.
	 *
	 * @return the top level folder
	 */
	@Override
	public TopLevelFolder getTopLevelFolder() { return this; }

	/**
	 * Gets the nature.
	 *
	 * @return the nature
	 */
	public String getNature() { return nature; }

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public VirtualContentType getType() { return VirtualContentType.VIRTUAL_FOLDER; }

}
