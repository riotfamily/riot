package org.riotfamily.website.performance;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface Compressor {

	public void compress(Reader in, Writer out) throws IOException;

}
