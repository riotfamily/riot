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
package org.riotfamily.components.view;

import java.util.Set;

import org.riotfamily.common.util.Generics;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.InvalidPropertyException;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;

/**
 * FreeMarker TemplateModel that first looks for a property on the wrapped 
 * facade. If no such property exists, it looks for a content value with
 * the same key. Finally, if no such content value is found, it looks for
 * a property on the facade's {@link ContentFacade#getOwner() owner}.
 */
public class ContentFacadeTemplateModel implements TemplateHashModelEx, TemplateScalarModel, AdapterTemplateModel {

	private ContentFacade facade;
	
	private BeansWrapper wrapper;
	
	private BeansWrapper strictWrapper;
	
	private BeanModel facadeModel;

	private SimpleHash contentModel;
	
	private BeanModel delegateModel;
	
	public ContentFacadeTemplateModel(ContentFacade facade, BeansWrapper wrapper) {
		this.facade = facade;
		this.wrapper = wrapper;
		
		strictWrapper = new BeansWrapper();
		strictWrapper.setStrict(true);
		strictWrapper.setOuterIdentity(wrapper);
		
		this.facadeModel = new BeanModel(facade, strictWrapper);
		this.delegateModel = new BeanModel(facade.getOwner(), wrapper);
	}
	
	public TemplateModel get(String key) throws TemplateModelException {
		TemplateModel result;
		try {
			result = facadeModel.get(key);
		}
		catch (InvalidPropertyException e1) {
			result = getContentModel().get(key);
			if (result == null) {
				result = delegateModel.get(key);
			}
		}
		return result;
	}
	
	private SimpleHash getContentModel() {
		if (contentModel == null) {
			contentModel = new SimpleHash(facade.getContent(), wrapper);
		}
		return contentModel;
	}

	@SuppressWarnings("unchecked")
	public Object getAdaptedObject(Class hint) {
		return facade.getOwner();
	}

	public String getAsString() throws TemplateModelException {
		return facade.getOwner().toString();
	}
	
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

	public TemplateCollectionModel keys() throws TemplateModelException {
		Set<Object> keys = Generics.newLinkedHashSet();
		add(delegateModel.keys(), keys);
		add(getContentModel().keys(), keys);
		add(facadeModel.keys(), keys);
		return new CollectionModel(keys, wrapper);
	}

	public TemplateCollectionModel values() throws TemplateModelException {
		Set<Object> values = Generics.newLinkedHashSet();
		add(delegateModel.values(), values);
		add(getContentModel().values(), values);
		add(facadeModel.values(), values);
		return new CollectionModel(values, wrapper);
	}
	
	private void add(TemplateCollectionModel col, Set<Object> set) throws TemplateModelException {
		TemplateModelIterator it = col.iterator();
		while (it.hasNext()) {
			set.add(it.next());
		}
	}

	public int size() throws TemplateModelException {
		return facadeModel.size() + contentModel.size() + delegateModel.size();
	}

}
