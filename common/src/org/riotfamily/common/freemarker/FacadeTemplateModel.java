package org.riotfamily.common.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;

public class FacadeTemplateModel extends StringModel {

	private Object delegate;
	
	public FacadeTemplateModel(Object facade, Object delegate, 
			BeansWrapper wrapper) {
		
		super(facade, wrapper);
		this.delegate = delegate;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getAdaptedObject(Class hint) {
		return delegate;
	}

	@Override
	public Object getWrappedObject() {
		return delegate;
	}
	
}
