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
package org.riotfamily.common.web.cache.freemarker;

import java.util.Collections;
import java.util.List;

import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.cache.CacheTagGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TaggingPluginObjectWrapper extends PluginObjectWrapper {

	private List<CacheTagGenerator> tagGenerators = Collections.emptyList();

	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		setTagGenerators(SpringUtils.orderedBeans(ctx, CacheTagGenerator.class));
		super.setApplicationContext(ctx);
	}
	
	private void setTagGenerators(List<CacheTagGenerator> tagGenerators) {
		Assert.notNull(tagGenerators, "tagGenerators must not be null");
		this.tagGenerators = tagGenerators;
	}

	@Override
	public TemplateModel wrap(Object obj) throws TemplateModelException {
		TemplateModel model = super.wrap(obj);
		if (obj != null) {
			for (CacheTagGenerator generator : tagGenerators) {
				String tag = generator.generateTag(obj);
				if (tag != null) {
					return TaggingTemplateModelProxy.newInstance(model, tag);
				}
			}
		}
		return model;
	}
}
