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
package org.riotfamily.common.web.mvc.scope;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.view.ModelPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

public class FlashModelExposer implements ModelPostProcessor {

	private Logger log = LoggerFactory.getLogger(FlashModelExposer.class);

	public void postProcess(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		FlashModel flash = FlashModel.get(request);
		if (flash != null) {
			for (Entry<String, Object> entry : flash.entrySet()) {
				if (model.containsKey(entry.getKey())) {
					Object existingValue = model.get(entry.getKey());
					if (ObjectUtils.nullSafeEquals(existingValue, entry.getValue())) {
						log.info("The model already contains the same value " +
								"that is to be exposed from FlashScope under key '{}': {}", entry.getKey(), existingValue);
					}
					else {
						log.error(String.format(
								"The model already contains a different value under the key '%s': %s -- Value in FlashScope is: %s",
								entry.getKey(), existingValue, entry.getValue()));
					}
				}
				else {
					log.debug("Exposing {} under key '{}'", entry.getKey(), entry.getValue());
					model.put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

}
