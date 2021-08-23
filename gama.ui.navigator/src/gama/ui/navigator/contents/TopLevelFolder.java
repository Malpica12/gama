/*********************************************************************************************
 *
 * 'TopLevelFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import gama.core.application.bundles.GamaBundleLoader;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.resources.IGamaIcons;
import gama.ui.base.resources.GamaColors.GamaUIColor;
import gama.ui.base.utils.ThemeHelper;
import one.util.streamex.StreamEx;

/**
 * Class TopLevelFolder.
 *
 * @author drogoul
 * @since 30 déc. 2015
 *
 */
public class TopLevelFolder extends VirtualContent<NavigatorRoot> implements IGamaIcons, IGamaColors {

	public enum Location {
		CoreModels, Plugins, Other, Unknown, Tests
	}

	WrappedProject[] children;
	final Image icon, statusIcon;
	final String statusMessage, nature;
	final GamaUIColor statusColor;
	final Location location;

	/**
	 * @param root
	 * @param name
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

	public void initializeChildren() {
		children = StreamEx.of(ResourcesPlugin.getWorkspace().getRoot().getProjects()).filter(this::privateAccepts)
				.map(p -> (WrappedProject) getManager().wrap(this, p)).toArray(WrappedProject.class);
	}

	@Override
	public boolean hasChildren() {
		return children.length > 0;
	}

	// @Override
	// public Font getFont() {
	// return GamaFonts.getNavigHeaderFont();
	// }

	@Override
	public Object[] getNavigatorChildren() {
		return children;
	}

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
	 * @param desc
	 * @return
	 */
	public final boolean privateAccepts(final IProject project) {
		if ((project == null) || !project.exists()) return false;
		// TODO This one is clearly a hack. Should be replaced by a proper way
		// to track persistently the closed projects
		if (!project.isOpen()) return estimateLocation(project.getLocation()) == location;
		try {
			return accepts(project.getDescription());
		} catch (final CoreException e) {
			return false;
		}
	}

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

	public final boolean accepts(final IProjectDescription desc) {
		if (nature != null) return desc.hasNature(nature);
		return desc.getNatureIds().length < 3;
	}

	@Override
	public final Image getImage() {
		return icon;
	}

	@Override
	public Image getStatusImage() {
		return statusIcon;
	}

	@Override
	public String getStatusMessage() {
		return statusMessage;
	}

	@Override
	public GamaUIColor getStatusColor() {
		return statusColor;
	}

	@Override
	public Color getColor() {
		return ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.GRAY_LABEL.color();
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		final var projectCount = children.length;
		sb.append(projectCount).append(" project");
		if (projectCount > 1) { sb.append("s"); }
	}

	@Override
	public ImageDescriptor getOverlay() {
		return DESCRIPTORS.get(findMaxProblemSeverity());
	}

	@Override
	public TopLevelFolder getTopLevelFolder() {
		return this;
	}

	public String getNature() {
		return nature;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.VIRTUAL_FOLDER;
	}

}
