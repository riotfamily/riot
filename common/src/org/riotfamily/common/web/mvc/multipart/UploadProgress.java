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
package org.riotfamily.common.web.mvc.multipart;

import org.riotfamily.common.util.FormatUtils;

public class UploadProgress {

	private String uploadId;
	
	private long bytesRead;
	
	private long bytesTotal;
	
	private long startTime;

	public UploadProgress(String uploadId, long bytesTotal) {
		this.uploadId = uploadId;
		this.bytesTotal = bytesTotal;
	}
	
	public boolean isComplete() {
		return bytesRead >= bytesTotal;
	}
	
	public int getProgress() {
		return (int) ((float) bytesRead / bytesTotal * 100);
	}
	
	public int getKbTransfered() {
		return (int) (bytesRead / 1024);
	}
	
	public String getDataTransfered() {
		return FormatUtils.formatByteSize(bytesRead);
	}
		
	public int getSecondsElapsed() {
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	public String getTransferRate() {
		if (getSecondsElapsed() == 0 || isComplete()) {
			return "";
		}
		return FormatUtils.formatByteSize(bytesRead / getSecondsElapsed()) + "/s";
	}

	void update(long bytesRead) {
		this.bytesRead = bytesRead;
		if (bytesRead >= bytesTotal) {
			ProgressMonitor.remove(uploadId);
		}
	}
}
