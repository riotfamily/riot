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
package org.riotfamily.core.screen;

import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mvc.view.ViewResolverHelper;
import org.riotfamily.common.web.support.DummyHttpServletResponse;
import org.riotfamily.forms.TemplateUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

public class ModelAndViewScreenlet implements Screenlet, ApplicationContextAware {

	private ViewResolverHelper viewResolverHelper;
	
	public void setApplicationContext(ApplicationContext context) {
        viewResolverHelper = new ViewResolverHelper(context);
    }
	
	public String render(ScreenContext context) throws Exception {
		StringWriter sw = new StringWriter();
		HttpServletResponse response = new DummyHttpServletResponse(sw);
		ModelAndView mv = handleRequest(context);
		HttpServletRequest request = context.getRequest();
		View view = viewResolverHelper.resolveView(request, mv);
		view.render(mv.getModel(), request, response);
		return sw.toString();
	}

	protected String getViewName() {
		return TemplateUtils.getTemplatePath(getTemplateClass());
	}
	
	protected Class<?> getTemplateClass() {
		return getClass();
	}

	protected ModelAndView handleRequest(ScreenContext context) {
		Map<String, Object> model = Generics.newHashMap();
		populateModel(model, context);
		ModelAndView mv = new ModelAndView(getViewName(), model);
		return mv;
	}

	protected void populateModel(Map<String, Object> model, 
			ScreenContext context) {
	}

}
