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
package org.riotfamily.common.freemarker;

import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Interface used by the {@link RiotFreeMarkerConfigurer} to support modular
 * post processing of the FreeMarker {@link Configuration}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface ConfigurationPostProcessor {

	public void postProcessConfiguration(Configuration config) 
			throws IOException, TemplateException;

}
