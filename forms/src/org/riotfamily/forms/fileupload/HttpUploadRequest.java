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
package org.riotfamily.forms.fileupload;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequestWrapper that returns a 
 * {@link org.riotfamily.forms.fileupload.CountingServletInputStream CountingInputStream}.
 */
public class HttpUploadRequest extends HttpServletRequestWrapper {
	
	private ServletInputStream wrappedStream;
	
	private CountingServletInputStream countingInputStream;
	
	public HttpUploadRequest(HttpServletRequest request) {
		super(request);
		this.countingInputStream = new CountingServletInputStream();
	}
		
	public CountingServletInputStream getCountingInputStream() {
		return countingInputStream;
	}
	
	public ServletInputStream getInputStream() throws IOException {
		if (wrappedStream == null) {
			wrappedStream = super.getInputStream();
			countingInputStream.setSourceStream(wrappedStream);
		}
		return countingInputStream;
	}
			
}
