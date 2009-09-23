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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.InvalidPropertyException;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class FacadeTemplateModel implements TemplateHashModel, TemplateScalarModel, AdapterTemplateModel {

	
	private static class StrictBeanModel extends BeanModel {

		public StrictBeanModel(Object object, BeansWrapper wrapper) {
			super(object, wrapper);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		protected TemplateModel invokeGenericGet(Map keyMap, Class clazz,
				String key) throws IllegalAccessException,
				InvocationTargetException, TemplateModelException {
			
			//Always return UNKNOWN
			return super.invokeGenericGet(Collections.EMPTY_MAP, clazz, key);
		}
	}
	
	private StrictBeanModel strictFacadeModel;
	
	private StrictBeanModel strictDelegateModel;
	
	private BeanModel genericFacadeModel;
	
	private Object delegate;
	
	public FacadeTemplateModel(Object facade, Object delegate, BeansWrapper wrapper) {
		BeansWrapper strictWrapper = new BeansWrapper();
		strictWrapper.setStrict(true);
		strictWrapper.setOuterIdentity(wrapper);
		this.strictFacadeModel = new StrictBeanModel(facade, strictWrapper);
		this.strictDelegateModel = new StrictBeanModel(delegate, strictWrapper);
		this.genericFacadeModel = new BeanModel(facade, wrapper);
		this.delegate = delegate;
	}
	
	public TemplateModel get(String key) throws TemplateModelException {
		TemplateModel result;
		try {
			result = strictFacadeModel.get(key);
		}
		catch (InvalidPropertyException ex) {
			try {
				result = strictDelegateModel.get(key);
			}
			catch (InvalidPropertyException e) {
				result = genericFacadeModel.get(key);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdaptedObject(Class hint) {
		return delegate;
	}

	public String getAsString() throws TemplateModelException {
		return delegate.toString();
	}
	
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}
	
}
