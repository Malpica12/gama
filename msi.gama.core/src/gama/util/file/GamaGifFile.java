/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGifFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.util.file;

import gama.common.util.ImageUtils;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.file;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.matrix.IMatrix;
import gaml.types.IType;

@file (
		name = "gif",
		extensions = { "gif" },
		buffer_type = IType.MATRIX,
		buffer_content = IType.INT,
		buffer_index = IType.POINT,
		concept = { IConcept.IMAGE, IConcept.FILE },
		doc = @doc ("GIF files represent a particular type of image files, which can be animated"))
public class GamaGifFile extends GamaImageFile {

	// private int averageDelay;
	// private int frameCount;
	@doc (value= "This file constructor allows to read a gif file",
			examples = {
					@example(value = "gif_file f <- gif_file(\"file.gif\");", isExecutable = false)
			})
	public GamaGifFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@doc (value= "This file constructor allows to store a matrix in a gif file (it does not save it - just store it in memory)",
			examples = {
					@example(value = "gif_file f <- gif_file(\"file.gif\",matrix([10,10],[10,10]));", isExecutable = false)
			})
	
	public GamaGifFile(final IScope scope, final String pathName, final IMatrix<Integer> image) {
		super(scope, pathName, image);

	}

	@Override
	public boolean isAnimated() {
		return getFrameCount() > 0;
	}

	public int getAverageDelay() {
		return ImageUtils.getInstance().getDuration(localPath) / getFrameCount();
	}

	public int getFrameCount() {
		return ImageUtils.getInstance().getFrameCount(localPath);
	}

	@Override
	protected String getHttpContentType() {
		return "image/gif";
	}

}
