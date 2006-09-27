package org.riotfamily.cachius.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

public class IOUtils {
	
	public static final int BUFFER_SIZE = 4096;
	
	private static Log log = LogFactory.getLog(IOUtils.class);
	
	public static int copy(InputStream in, OutputStream out) 
			throws IOException {
		
		try {
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		}
		finally {
			closeStream(in);
		}
	}
	
	public static int copy(Reader in, Writer out) 
			throws IOException {
		
		try {
			int byteCount = 0;
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		}
		finally {
			closeReader(in);
		}
	}
	
	public static void closeStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
			}
			catch (IOException ex) {
			}
		}
	}
	
	public static void closeStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			}
			catch (IOException ex) {
			}
		}
	}
	
	public static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			}
			catch (IOException ex) {
			}
		}
	}
	
	public static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			}
			catch (IOException ex) {
			}
		}
	}

	public static void move(File source, File dest) {
		if (!source.renameTo(dest)) {
			try {
				log.info("Unable to move file " + source);
				FileCopyUtils.copy(source, dest);
			}
			catch (IOException e) {
				log.warn("Copy failed.", e);
			}
	    	delete(source);
	    }
	}
	
	public static void delete(File file) {
		if (file != null && file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				file.deleteOnExit();
			}
		}
	}
}
