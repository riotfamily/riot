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
package org.riotfamily.common.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class DummyHttpServletResponse implements HttpServletResponse {

	private OutputStream targetStream;
	
	private Writer targetWriter;
	
	private PrintWriter writer;

	private ServletOutputStream outputStream;
	
	private Locale locale;

	private String contentType;
	
	private String encoding = "UTF-8";

	
	public DummyHttpServletResponse(OutputStream targetStream) {
		this.targetStream = targetStream;
	}
	
	public DummyHttpServletResponse(Writer targetWriter) {
		this.targetWriter = targetWriter;
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always returns <code>false</code>.
	 */
	public boolean containsHeader(String name) {
		return false;
	}

	/**
	 * Returns the passed parameter as-is.
	 */
	public String encodeRedirectURL(String url) {
		return url;
	}

	/**
	 * Returns the passed parameter as-is.
	 */
	public String encodeRedirectUrl(String url) {
		return url;
	}

	/**
	 * Returns the passed parameter as-is.
	 */
	public String encodeURL(String url) {
		return url;
	}

	/**
	 * Returns the passed parameter as-is.
	 */
	public String encodeUrl(String url) {
		return url;
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void sendError(int sc) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws an {@link UnsupportedOperationException}.
	 */
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Does nothing.
	 */
	public void setContentLength(int len) {
	}
	
	/**
	 * Does nothing.
	 */
	public void setStatus(int sc) {
	}

	/**
	 * Does nothing.
	 */
	public void setStatus(int sc, String sm) {
	}

	/**
	 * Flushes the OutputStream/Writer.
	 */
	public void flushBuffer() throws IOException {
		if (outputStream != null) {
			outputStream.flush();
		}
		if (writer != null) {
			writer.flush();
		}
	}

	/**
	 * Always returns <code>0</code>.
	 */
	public int getBufferSize() {
		return 0;
	}

	/**
	 * Returns the encoding set via {@link #setCharacterEncoding(String)}.
	 */
	public String getCharacterEncoding() {
		return encoding;
	}

	/**
	 * Returns the content type set via {@link #setContentType(String)}.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns the locale set via {@link #setLocale(Locale)}.
	 */
	public Locale getLocale() {
		return locale;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (outputStream == null) {
			if (targetStream == null) {
				throw new java.lang.IllegalStateException(
						"The wrapper was set up to capture character data");
			}
			outputStream = new DelegatingServletOutputStream(targetStream);
		}
		return outputStream;
	}
	
	public PrintWriter getWriter() throws IOException {
		if (writer == null) {
			if (targetWriter != null) {
				writer = new PrintWriter(targetWriter);
			}
			else {
				writer = new PrintWriter(new OutputStreamWriter(targetStream,
						getCharacterEncoding()));
			}
		}
		return writer;
	}

	/**
	 * Always returns <code>false</code>.
	 */
	public boolean isCommitted() {
		return false;
	}

	/**
	 * Does nothing.
	 */
	public void reset() {
	}

	/**
	 * Does nothing.
	 */
	public void resetBuffer() {
	}

	/**
	 * Does nothing.
	 */
	public void setBufferSize(int size) {
	}

	/**
	 * Sets the encoding. Can retrieved via {@link #getCharacterEncoding()}.
	 */
	public void setCharacterEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Sets the content type. Can retrieved via {@link #getContentType()}.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the locale. Can retrieved via {@link #getLocale()}.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
