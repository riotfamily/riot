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

import java.util.List;
import java.util.Map;

import org.riotfamily.cachius.CacheContext;
import org.riotfamily.common.util.Generics;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MapModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TaggingMapModel extends MapModel {

	private List<String> tags = Generics.newArrayList();
	
	public TaggingMapModel(Map<?,?> map, BeansWrapper wrapper) {
		super(map, wrapper);
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}
	
	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		for (String tag : tags) {
			CacheContext.tag(tag);
		}
		return super.get(key);
	}

}
