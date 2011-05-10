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
package org.riotfamily.core.form.element;

import java.io.PrintWriter;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.screen.DefaultScreenContext;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenRepository;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.form.FormScreen;
import org.riotfamily.core.screen.list.ChooserSettings;
import org.riotfamily.forms.element.select.AbstractChooser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ObjectChooser extends AbstractChooser 
		implements ApplicationContextAware {

	private String rootId;
	
	private String targetId;
	
	private String rootProperty;
	
	private String rootIdAttribute;
	
	private ScreenRepository screenRepository;
	
	private ListScreen rootList;
	
	private ListScreen targetList;
	
	private Class<?> targetClass;

	private ApplicationContext applicationContext;
	
	public ObjectChooser(ScreenRepository screenRepository) {
		this.screenRepository = screenRepository;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	public void setRootProperty(String rootProperty) {
		this.rootProperty = rootProperty;
	}

	public void setRootIdAttribute(String rootIdAttribute) {
		this.rootIdAttribute = rootIdAttribute;
	}
	
	@Override
	protected void afterBindingSet() {
		targetClass = getEditorBinding().getPropertyType();
	}

	@Override
	protected void afterFormSet() {
		targetList = ScreenUtils.getListScreen(screenRepository.getScreen(targetId));
		if (rootId != null) {
			rootList = screenRepository.getScreen(rootId, ListScreen.class);
		}
		else {
			rootList = ScreenUtils.getRootListScreen(targetList);
		}
	}
	
	protected String getRootObjectId() {
		String id = null;
		if (rootId != null) {
			if (rootIdAttribute != null) {
				id = String.valueOf(getForm().getAttribute(rootIdAttribute));
			}
			else {
				Object root = FormScreen.getScreenContext(getForm()).getParent();
				if (rootProperty != null) {
					root = PropertyUtils.getProperty(root, rootProperty);
				}
				if (root != null) {
					id = ScreenUtils.getParentListScreen(rootList).getDao().getObjectId(root);
				}
			}
		}
		return id;
	}
	
	@Override
	protected String getChooserUrl() {
		ChooserSettings settings = new ChooserSettings(targetId, rootId, targetClass);
		String url = HandlerUrlUtils.getUrlResolver(applicationContext)
				.getUrlForHandler(rootList.getId(),
				new DefaultScreenContext(null, null, null, getRootObjectId(), false));
		
		return settings.appendTo(url);
	}

	@Override
	protected Object loadBean(String objectId) {
		return targetList.getDao().load(objectId);
	}

	@Override
	protected void renderLabel(Object object, PrintWriter writer) {
		writer.print(targetList.getItemLabel(object));
	}

}
