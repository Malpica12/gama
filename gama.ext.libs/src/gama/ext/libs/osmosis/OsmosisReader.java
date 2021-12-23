/*******************************************************************************************************
 *
 * OsmosisReader.java, in gama.ext.libs, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.libs.osmosis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * Glue code that implements a task that connects an InputStream a containing binary-format data to a Sink.
 *
 * @author crosby
 *
 */
public class OsmosisReader implements RunnableSource {

	/** The sink. */
	private Sink sink;
	/** Store the input stream we're using. */
	final InputStream input;
	/** The binary parser object. */
	final OsmosisBinaryParser parser;

	/**
	 * Make a reader based on a target input stream.
	 *
	 * @param input
	 *            The input stream to read from.
	 */
	public OsmosisReader(final InputStream input) {
		if (input == null) { throw new Error("Null input"); }
		this.input = input;
		parser = new OsmosisBinaryParser();
	}

	@Override
	public void setSink(final Sink sink) {
		this.sink = sink;
		parser.setSink(sink);
	}

	@Override
	public void run() {
		try {
			sink.initialize(Collections.<String, Object> emptyMap());

			new BlockInputStream(input, parser).process();

		} catch (final IOException e) {
			throw new OsmosisRuntimeException("Unable to process PBF stream", e);
		} finally {
			sink.complete();
		}
	}

}