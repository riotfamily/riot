/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.SocketException;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

public class IOUtils {
	
	public static final int BUFFER_SIZE = 4096;
	
	private IOUtils() {
	}
	
	private static Logger getLog() {
		return LoggerFactory.getLogger(IOUtils.class);
	}
	
	/**
	 * Copies the content of the given InputStream to an OutputStream.
	 * Unlike {@link FileCopyUtils#copy(InputStream, OutputStream)} this method
	 * does not close the OutputStream (only the InputStream).
	 * @param in the stream to copy from
	 * @param out the stream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
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
	
	/**
	 * Copies the content of the given InputStream to an OutputStream,
	 * swallowing exceptions caused by a ClientAbortException.
	 * 
	 * @see #copy(InputStream, OutputStream)
	 */
	public static int serve(InputStream in, OutputStream out) 
			throws IOException {
	
		try {
			return copy(in, out);
		}
		catch (SocketException e) {
		}
		catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
		}
		return -1;
	}
	
	/**
	 * Copies the content of the given File to an OutputStream.
	 *  
	 * @see #copy(InputStream, OutputStream)
	 */
	public static int copy(File file, OutputStream out) throws IOException {
		return copy(new BufferedInputStream(new FileInputStream(file)), out);
	}
	
	/**
	 * Copies the content of the given File to an OutputStream,
	 * swallowing exceptions caused by a ClientAbortException.
	 *  
	 * @see #copy(File, OutputStream)
	 */
	public static int serve(File file, OutputStream out) throws IOException {
		try {
			return copy(file, out);
		}
		catch (SocketException e) {
		}
		catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
		}
		return -1;
	}
	
	/**
	 * Copies the content of the given Reader to a Writer.
	 * Unlike {@link FileCopyUtils#copy(Reader, Writer)} this method does not 
	 * close the Writer (only the Reader).
	 * @param in the Reader to copy from
	 * @param out the Writer to copy to
	 * @return the number of characters copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(Reader in, Writer out) throws IOException {
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
	
	/**
	 * Copies the content of the given Reader to a Writer,
	 * swallowing exceptions caused by a ClientAbortException.
	 * 
	 * @see #copy(Reader, Writer)
	 */
	public static int serve(Reader in, Writer out) throws IOException {
		try {
			return copy(in, out);
		}
		catch (SocketException e) {
		}
		catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
		}
		return -1;
	}
	
	/**
	 * Copies the content of the given InputStream to a Writer.
	 * 
	 * @see #copy(Reader, Writer)
	 */
	public static int copy(InputStream in, Writer out, String encoding) 
			throws IOException {
		
		try {
			return copy(new InputStreamReader(in, encoding), out);
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * Copies the content of the given InputStream to a Writer,
	 * swallowing exceptions caused by a ClientAbortException.
	 * 
	 * @see #copy(InputStream, Writer, String)
	 */
	public static int serve(InputStream in, Writer out, String encoding) 
			throws IOException {

		try {
			return copy(in, out, encoding);
		}
		catch (SocketException e) {
		}
		catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
		}
		return -1;
	}
	
	/**
	 * Copies the content of the given File to a Writer.
	 * 
	 * @see #copy(InputStream, Writer, String)
	 */
	public static int copy(File file, Writer out, String encoding) 
			throws IOException {
		
		return copy(new BufferedInputStream(new FileInputStream(file)), 
				out, encoding);
	}
	
	/**
	 * Copies the content of the given File to a Writer,
	 * swallowing exceptions caused by a ClientAbortException.
	 * 
	 * @see #copy(File, Writer, String)
	 */
	public static int serve(File file, Writer out, String encoding) 
			throws IOException {

		try {
			return copy(file, out, encoding);
		}
		catch (SocketException e) {
		}
		catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
		}
		return -1;
	}
	
	/**
	 * Writes the content of the given buffer to an OutputStream,
	 * swallowing exceptions caused by a ClientAbortException.
	 */
	public static void serve(byte[] buffer, OutputStream out) throws IOException {
		try {
			out.write(buffer);
		}
		catch (SocketException e) {
		}
		catch (IOException e) {
			if (!SocketException.class.isInstance(e.getCause())) {
				throw e;
			}
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
				getLog().info("Unable to move file " + source);
				FileCopyUtils.copy(source, dest);
			}
			catch (IOException e) {
				getLog().warn("Copy failed.", e);
			}
	    	delete(source);
	    }
	}
	
	public static void clear(File file) throws IOException {
    	new FileOutputStream(file).close();
	}
	
	public static void delete(File file) {
		if (file != null && file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				file.deleteOnExit();
			}
		}
	}
	
	public static void clearDirectory(File f) {
        if (f.isDirectory()) {
            File[] entries = f.listFiles();
            for (int i = 0; i < entries.length; i++) {
            	deleteRecursive(entries[i]);
            }
        }
    }
	
	public static void deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] entries = f.listFiles();
            for (int i = 0; i < entries.length; i++) {
            	deleteRecursive(entries[i]);
            }
        }
        f.delete();
    }
	
	public static String exec(String command, String... args) throws IOException {
		List<String> argList = null;
		if (args != null) {
			argList = Generics.newArrayList();
			for (String arg : args) {
				argList.add(arg);
			}
		}
		return exec(command, argList);
	}
	
	public static String exec(String command, List<String> args) throws IOException {
		List<String> commandLine = Generics.newArrayList();
		commandLine.add(command);
		if (args != null) {
			commandLine.addAll(args);
		}
		Process p = new ProcessBuilder(commandLine).redirectErrorStream(true).start();
		Reader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringWriter sw = new StringWriter(); 
		copy(reader, sw);
		return sw.toString();
	}
}
