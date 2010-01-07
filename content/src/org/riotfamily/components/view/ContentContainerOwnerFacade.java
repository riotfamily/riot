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
package org.riotfamily.components.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.cache.tags.CacheTagUtils;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentContainerOwner;
import org.riotfamily.components.support.EditModeUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class ContentContainerOwnerFacade implements ContentFacade {
	
	protected ContentContainerOwner owner;

	protected HttpServletRequest request;
	
	protected HttpServletResponse response;
	
	protected boolean preview;
		
	public ContentContainerOwnerFacade(ContentContainerOwner owner, 
			HttpServletRequest request, HttpServletResponse response) {
		
		this.owner = owner;
		this.request = request;
		this.response = response;
		this.preview = isPreview(owner);
	}
	
	protected HttpServletRequest getRequest() {
		return request;
	}

	protected HttpServletResponse getResponse() {
		return response;
	}

	protected boolean isPreview(ContentContainerOwner owner) {
		return EditModeUtils.isPreview(request, owner.getContentContainer());
	}
			
	public ContentContainer getContentContainer() {
		ContentContainer container = owner.getContentContainer();
		CacheTagUtils.tag(container);
		return container;
	}

	public Content getContent() {
		Content content = getContentContainer().getContent(preview);
		CacheTagUtils.tag(content);
		return content;
	}
	
	public Object getOwner() {
		return owner;
	}
	
	@Override
	public String toString() {
		return owner.toString();
	}

}
