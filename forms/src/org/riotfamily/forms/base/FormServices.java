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
package org.riotfamily.forms.base;

import org.riotfamily.common.ui.RenderingService;
import org.riotfamily.forms.option.IdentityReferenceAdapter;
import org.riotfamily.forms.option.ReferenceService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;

public class FormServices {

	private ConversionService conversionService;
	
	private RenderingService renderingService;
	
	private ReferenceService referenceService;

	public ConversionService getConversionService() {
		if (conversionService == null) {
			conversionService = ConversionServiceFactory.createDefaultConversionService();
		}
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public RenderingService getRenderingService() {
		if (renderingService == null) {
			renderingService = RenderingService.newInstance(getConversionService());
		}
		return renderingService;
	}

	public void setRenderingService(RenderingService renderingService) {
		this.renderingService = renderingService;
	}

	public ReferenceService getReferenceService() {
		if (referenceService == null) {
			referenceService = new ReferenceService(new IdentityReferenceAdapter());
		}
		return referenceService;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}
	
}
