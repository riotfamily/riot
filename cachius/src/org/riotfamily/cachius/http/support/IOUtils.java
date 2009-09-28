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
package org.riotfamily.cachius.http.support;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.SocketException;

import org.springframework.util.FileCopyUtils;

public class IOUtils {
	
	public static final int BUFFER_SIZE = 4096;
	
	private IOUtils() {
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
	
	public static int copy(Reader in, Writer out, int length) throws IOException {
		for (int i = 0; i < length; i++) {
			int c = in.read();
			if (c == -1) {
				return i;
			}
			out.write(c);
		}
		return length;
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

}
