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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.riotfamily.common.util.Generics;

public class ProgressMonitor {

	private static AtomicInteger nextUploadId = new AtomicInteger();
	
	private static Map<String, UploadProgress> progressMap = Generics.newHashMap();
	
	public static String nextUploadId() {
		return String.valueOf(nextUploadId.incrementAndGet());
	}
	
	static UploadProgress newProgress(String uploadId, int contentLength) {
		UploadProgress progress = new UploadProgress(uploadId, contentLength);
		progressMap.put(uploadId, progress);
		return progress;
	}
	
	static void remove(String uploadId) {
		progressMap.remove(uploadId);
	}
	
	public static UploadProgress getProgress(String uploadId) {
		return progressMap.get(uploadId);
	}

}
