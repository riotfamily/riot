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

import org.riotfamily.common.web.support.CapturingResponseWrapper;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ContentMap;
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
	
	private ComponentList createList(final ContentMap contentMap, String key, 
			ComponentListConfig config) {
		
		final ComponentList list = new ComponentList(contentMap, key);
		contentMap.put(key, list);
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
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				contentMap.getContent().save();
			}
		});
		return list;
	}
	
	public String renderComponents(ContentMap contentMap, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list;
		RenderStrategy strategy = liveModeRenderStrategy;
		list = (ComponentList) contentMap.get(key);
		if (EditModeUtils.isEditMode(request)) {
			if (list == null) {
				list = createList(contentMap, key, config);
			}
			if (AccessController.isGranted("edit", 
					contentMap.getContent().getContainer().getOwner(), request)) {
				
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
