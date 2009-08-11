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
package org.riotfamily.components.render.list;

import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.CapturingResponseWrapper;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class ComponentListRenderer {
	
	private PlatformTransactionManager transactionManager;
	
	private RenderStrategy liveModeRenderStrategy;
	
	private RenderStrategy editModeRenderStrategy;
	
	private ComponentMetaDataProvider metaDataProvider;
	
	
	public ComponentListRenderer(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setLiveModeRenderStrategy(RenderStrategy liveModeRenderStrategy) {
		this.liveModeRenderStrategy = liveModeRenderStrategy;
	}

	public void setEditModeRenderStrategy(RenderStrategy editModeRenderStrategy) {
		this.editModeRenderStrategy = editModeRenderStrategy;
	}
	
	public void setMetaDataProvider(ComponentMetaDataProvider metaDataProvider) {
		this.metaDataProvider = metaDataProvider;
	}
	
	private ComponentList createList(final Content content, String key, 
			ComponentListConfig config) {
		
		final ComponentList list = new ComponentList(content);
		content.put(key, list);
		if (config.getInitialTypes() != null) {
			for (String type : config.getInitialTypes()) {
				Component component = new Component(list);
				component.setType(type);
				Map<String, Object> defaults = metaDataProvider.getMetaData(type).getDefaults();
				if (defaults != null) {
					component.putAll(defaults);
				}
				list.add(component);
			}
		}
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				content.save();
			}
		});
		return list;
	}
	
	public String renderComponentList(ContentContainer container, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list = null;
		RenderStrategy strategy = liveModeRenderStrategy;
		if (EditModeUtils.isPreview(request, container)) {
			list = (ComponentList) container.getPreviewVersion().get(key);
			if (EditModeUtils.isEditMode(request)) {
				if (list == null) {
					list = createList(container.getPreviewVersion(), key, config);
					
					// If the new list is not empty, we have to store it and mark
					// the container as dirty.
					if (list.size() > 0) {
						container.getPreviewVersion().put(key, list);
						container.setDirty(true);
					}
				}
				if (AccessController.isGranted("edit", container)) {
					strategy = editModeRenderStrategy;
				}
			}
		}
		else if (container.getLiveVersion() != null) {
			list = (ComponentList) container.getLiveVersion().get(key);
		}
		
		StringWriter sw = new StringWriter();
		if (list != null) {
			strategy.render(list, config, request, new CapturingResponseWrapper(response, sw));
		}
		return sw.toString();
	}
	
	public String renderNestedComponentList(Component component, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list;
		RenderStrategy strategy = liveModeRenderStrategy;
		list = (ComponentList) component.get(key);
		if (EditModeUtils.isEditMode(request)) {
			if (list == null) {
				list = createList(component.getOwner(), key, config);
			}
			//TODO Pass the root container instead ...
			if (AccessController.isGranted("edit", list)) {
				strategy = editModeRenderStrategy;
			}
		}
		
		StringWriter sw = new StringWriter();
		if (list != null) {
			strategy.render(list, config, request, new CapturingResponseWrapper(response, sw));
		}
		return sw.toString();
	}

}
