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
package org.riotfamily.common.web.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

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
	 * Sets the length of the content body in the response
	 * In HTTP servlets, this method sets the HTTP Content-Length header.
	 *
	 * @param len a long specifying the length of the
	 *            content being returned to the client; sets the Content-Length header
	 * @since Servlet 3.1
	 */
	@Override
	public void setContentLengthLong(long len) {

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

	public int getStatus() {
		return SC_OK;
	}

	public String getHeader(String name) {
		return null;
	}

	public Collection<String> getHeaders(String name) {
		return Collections.emptyList();
	}

	public Collection<String> getHeaderNames() {
		return Collections.emptyList();
	}

	/**
	 * Sets the supplier of trailer headers.
	 *
	 * <p>The trailer header field value is defined as a comma-separated list
	 * (see Section 3.2.2 and Section 4.1.2 of RFC 7230).</p>
	 *
	 * <p>The supplier will be called within the scope of whatever thread/call
	 * causes the response content to be completed. Typically this will
	 * be any thread calling close() on the output stream or writer.</p>
	 *
	 * <p>The trailers that run afoul of the provisions of section 4.1.2 of
	 * RFC 7230 are ignored.</p>
	 *
	 * <p>The RFC requires the name of every key that is to be in the
	 * supplied Map is included in the comma separated list that is the value
	 * of the "Trailer" response header.  The application is responsible for
	 * ensuring this requirement is met.  Failure to do so may lead to
	 * interoperability failures.</p>
	 *
	 * @param supplier the supplier of trailer headers
	 * @throws IllegalStateException if it is invoked after the response has
	 *                               has been committed,
	 *                               or the trailer is not supported in the request, for instance,
	 *                               the underlying protocol is HTTP 1.0, or the response is not
	 *                               in chunked encoding in HTTP 1.1.
	 * @implSpec The default implementation is a no-op.
	 * @since Servlet 4.0
	 */
	@Override
	public void setTrailerFields(Supplier<Map<String, String>> supplier) {

	}

	/**
	 * Gets the supplier of trailer headers.
	 *
	 * @return <code>Supplier</code> of trailer headers
	 * @implSpec The default implememtation return null.
	 * @since Servlet 4.0
	 */
	@Override
	public Supplier<Map<String, String>> getTrailerFields() {
		return null;
	}

}
