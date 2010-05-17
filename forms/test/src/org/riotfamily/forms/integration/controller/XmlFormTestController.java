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
package org.riotfamily.forms.integration.controller;

import java.util.List;

import javax.annotation.Resource;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.integration.model.TestBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/form/xml")
public class XmlFormTestController extends AbstractFormTestController {

	@Resource
	private TestBean testBean;

	@Resource
	private List<Element> formElements;
	
	@Override
	protected Object getBackingObject() {
		return testBean;
	}
	
	@Override
	protected Form createForm() {
		Form f = new Form();
		for (Element el : formElements) {
			f.add(el);
		}
		return f;
	}
}
