package org.riotfamily.common.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class NullOutputStream extends OutputStream {

	public void write(int b) throws IOException {
	}

}
