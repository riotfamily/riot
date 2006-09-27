package org.riotfamily.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * FileInputStream that deletes the underlying file when the stream is
 * closed or finalized. 
 */
public class TempFileInputStream extends FileInputStream {

	private File file;

	public TempFileInputStream(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}

	public void close() throws IOException {
		super.close();
		deleteFile();
	}

	protected void finalize() throws IOException {
		super.finalize();
		deleteFile();
	}

	private void deleteFile() {
		if (file.exists()) {
			file.delete();
		}
	}

}
