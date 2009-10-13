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

import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenRepository;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.list.ChooserSettings;
import org.riotfamily.forms.element.select.AbstractChooser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ObjectChooser extends AbstractChooser 
		implements ApplicationContextAware {

	private String rootId;
	
	private String targetId;
	
	private ScreenRepository screenRepository;
	
	private ListScreen rootList;
	
	private ListScreen targetList;

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
	
	@Override
	protected void afterFormSet() {
		targetList = screenRepository.getScreen(targetId, ListScreen.class);
		if (rootId != null) {
			rootList = screenRepository.getScreen(rootId, ListScreen.class);
		}
		else {
			rootList = ScreenUtils.getRootListScreen(targetList);
		}
	}
	
	@Override
	protected String getChooserUrl() {
		ChooserSettings settings = new ChooserSettings(targetId, rootId, null);
		String url = HandlerUrlUtils.getUrlResolver(applicationContext)
				.getUrlForHandler(rootList.getId(),
				new ScreenContext(null, null, null, null, false));
		return settings.appendTo(url);
	}

	@Override
	protected Object loadBean(String objectId) {
		return targetList.getDao().load(objectId);
	}

	@Override
	protected void renderLabel(Object object, PrintWriter writer) {
		if (object != null) {
			writer.print(targetList.getItemLabel(object));
		}
	}

}
