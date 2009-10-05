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

/**
 * ServletInputStream that counts the number of bytes read. This class is used
 * to provide progress information for file uploads.
 */
public class CountingServletInputStream extends ServletInputStream {

	private ServletInputStream sourceStream;

	private long bytesRead;
	
	public CountingServletInputStream() {
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
