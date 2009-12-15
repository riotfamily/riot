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
package org.riotfamily.components.index;

import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.RiotFileReference;
import org.riotfamily.media.model.RiotFile;

public class FileReferenceUpdater extends ContentIndexerSupport {

	@Override
	public void contentCreated(Content content) {
		createIndex(content);
	}

	@Override
	public void contentDeleted(Content content) {
		deleteIndex(content);
	}

	@Override
	public void contentModified(Content content) {
		deleteIndex(content);
		createIndex(content);
	}
	
	private void createIndex(Content content) {
		for (Object obj : content.getReferences()) {
			if (obj instanceof RiotFile) {
				new RiotFileReference(content, (RiotFile) obj).save();
			}
		}
	}

	private void deleteIndex(Content content) {
		RiotFileReference.deleteByContent(content);
	}

}
