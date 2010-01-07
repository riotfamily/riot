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
package org.riotfamily.common.web.cache.freemarker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.riotfamily.cachius.CacheContext;
import org.springframework.util.ClassUtils;

import freemarker.template.TemplateModel;

/**
 * Dynamic proxy that tags the current cache item if one of the following 
 * methods is invoked:
 * <ul>
 * <li>get</li>
 * <li>size</li>
 * <li>getAsString</li>
 * <li>getAdaptedObject</li>
 * <li>getWrappedObject</li>
 * </ul>
 */
public class TaggingTemplateModelProxy implements InvocationHandler {

	private static List<String> taggingMethods = Arrays.asList(new String[] {
			"get", 
			"size",
			"getAsString",
			"getAdaptedObject",
			"getWrappedObject"
	});
	
	private TemplateModel delegate;
	
	private String tag;
	
	private TaggingTemplateModelProxy(TemplateModel delegate, String tag) {
		this.delegate = delegate;
		this.tag = tag;
	}	
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		if (isTaggingMethod(method)) {
			CacheContext.tag(tag);
		}
		return method.invoke(delegate, args);
	}

	private boolean isTaggingMethod(Method method) {
		return taggingMethods.contains(method.getName());
	}

	public static TemplateModel newInstance(TemplateModel model, String tag) {
		return (TemplateModel) Proxy.newProxyInstance(
				model.getClass().getClassLoader(), 
				ClassUtils.getAllInterfaces(model),
				new TaggingTemplateModelProxy(model, tag));
	}
}
