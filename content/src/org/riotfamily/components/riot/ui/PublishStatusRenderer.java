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
package org.riotfamily.components.riot.ui;

import org.riotfamily.common.ui.TypedRenderer;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentContainerOwner;

public class PublishStatusRenderer extends TypedRenderer<ContentContainerOwner> {

	@Override
	protected String render(ContentContainerOwner owner) {
		return String.format("<div class=\"publish-status publish-status-%s\"></div>",  getStyleClass(owner));
	}
	
	protected String getStyleClass(ContentContainerOwner owner) {
		ContentContainer container = owner.getContentContainer();
		if (container.getLiveVersion() == null) {
			return "new";
		}
		if (container.getPreviewVersion().isDirty()) {
			return "dirty";
		}
		return "published";
	}

}
