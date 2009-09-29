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
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StringCapturingResponseWrapper extends HttpServletResponseWrapper {

	private StringWriter stringWriter;
	
	private PrintWriter printWriter;
	
	public StringCapturingResponseWrapper(HttpServletResponse response) {
		super(response);
		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);
	}
	
	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}
	
	public String getCapturedData() {
		printWriter.flush();
		return stringWriter.toString();
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		throw new IOException("This ResponseWrapper must only be used " +
				"for character data.");
	}
	
}
