package org.riotfamily.website.template;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.MacroHelperFactory;

public class TemplateMacroHelperFactory implements MacroHelperFactory {

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {
		
		return new TemplateMacroHelper();
	}

}
