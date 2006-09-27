package org.riotfamily.common.web.view.freemarker;

import java.util.List;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public abstract class AbstractSimpleMethod implements TemplateMethodModel {

	public final Object exec(List args) throws TemplateModelException {
		if (args.size() != 1) {
			throw new TemplateModelException("Invalid number of arguments.");
		}
		try {
			return exec(args.get(0));
		}
		catch (Exception e) {
			throw new TemplateModelException(e);
		}
	}
	
	protected abstract Object exec(Object arg) throws Exception;

}
