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
package org.riotfamily.cachius.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.riotfamily.cachius.support.SessionIdEncoder;

public class SessionIdInsertingResponseWrapper extends HttpServletResponseWrapper {

	private SessionIdEncoder sessionIdEncoder;
	
	public SessionIdInsertingResponseWrapper(HttpServletResponse response, 
			SessionIdEncoder sessionIdEncoder) {
		
		super(response);
		this.sessionIdEncoder = sessionIdEncoder;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(sessionIdEncoder.createIdInsertingWriter(super.getWriter()));
	}
	
}
