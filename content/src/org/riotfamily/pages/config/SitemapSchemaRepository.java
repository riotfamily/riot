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
package org.riotfamily.pages.config;

import java.util.Collection;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;

public class SitemapSchemaRepository implements OptionsModel {

	private Map<String, SitemapSchema> schemas = Generics.newHashMap();

	private SitemapSchema defaultSchema;
	
	public void addSchema(SitemapSchema schema) {
		schemas.put(schema.getName(), schema);
		if (defaultSchema == null) {
			defaultSchema = schema;
		}
	}

	public SitemapSchema getDefaultSchema() {
		return defaultSchema;
	}
	
	public String getDefaultSchemaName() {
		return defaultSchema != null ? defaultSchema.getName() : null;
	}
	
	public SitemapSchema getSchema(String id) {
		if (id == null) {
			return defaultSchema;
		}
		return schemas.get(id);
	}
	
	public Collection<SitemapSchema> getSchemas() {
		return schemas.values();
	}

	public Collection<?> getOptionValues(Element element) {
		return schemas.values();
	}

}
