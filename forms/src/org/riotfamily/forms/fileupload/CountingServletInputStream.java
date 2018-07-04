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

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * ServletInputStream that counts the number of bytes read. This class is used
 * to provide progress information for file uploads.
 */
public class CountingServletInputStream extends ServletInputStream {

	private ServletInputStream sourceStream;

	private long bytesRead;
	
	public CountingServletInputStream() {
	}

	/**
	 * Returns true when all the data from the stream has been read else
	 * it returns false.
	 *
	 * @return <code>true</code> when all data for this particular request
	 * has been read, otherwise returns <code>false</code>.
	 * @since Servlet 3.1
	 */
	@Override
	public boolean isFinished() {
		return false;
	}

	/**
	 * Returns true if data can be read without blocking else returns
	 * false.
	 *
	 * @return <code>true</code> if data can be obtained without blocking,
	 * otherwise returns <code>false</code>.
	 * @since Servlet 3.1
	 */
	@Override
	public boolean isReady() {
		return false;
	}

	/**
	 * Instructs the <code>ServletInputStream</code> to invoke the provided
	 * {@link ReadListener} when it is possible to read
	 *
	 * @param readListener the {@link ReadListener} that should be notified
	 *                     when it's possible to read.
	 * @throws IllegalStateException if one of the following conditions is true
	 *                               <ul>
	 *                               <li>the associated request is neither upgraded nor the async started
	 *                               <li>setReadListener is called more than once within the scope of the same request.
	 *                               </ul>
	 * @throws NullPointerException  if readListener is null
	 * @since Servlet 3.1
	 */
	@Override
	public void setReadListener(ReadListener readListener) {

	}

	public void setSourceStream(ServletInputStream sourceStream) {
		this.sourceStream = sourceStream;
	}
	
	public int read() throws IOException {
		if (sourceStream == null) {
			throw new IllegalMonitorStateException("No sourceStream set");
		}
		int i = sourceStream.read();
		if (i >= 0) {
			bytesRead ++;
		}
		return i;
	}

	public long getBytesRead() {
		return bytesRead;
	}
}
