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
package org.riotfamily.common.ui;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class SpringConversionRenderer implements Renderer {

	private static TypeDescriptor TARGET_TYPE = TypeDescriptor.valueOf(String.class);
	
	private ConversionService conversionService;

	@Autowired
	public SpringConversionRenderer(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	public boolean supports(TypeDescriptor typeDescriptor) {
		return conversionService.canConvert(typeDescriptor, TARGET_TYPE);
	}
	
	public String render(Object object, TypeDescriptor typeDescriptor) {
		String s = (String) conversionService.convert(object, typeDescriptor, TARGET_TYPE);
		return FormatUtils.xmlEscape(s);
	}
	
}
