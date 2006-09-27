package org.riotfamily.common.web.view.freemarker;

import java.util.List;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.view.ViewContext;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class OriginalRequestUriMethod implements TemplateMethodModel {
	
	public Object exec(List args) throws TemplateModelException {
		return ServletUtils.getOriginalRequestUri(ViewContext.getRequest());
	}

}
