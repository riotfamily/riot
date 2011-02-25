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
package org.riotfamily.components.xstream;

import org.riotfamily.pages.config.SystemPageType;
import org.riotfamily.pages.config.VirtualPageType;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.VirtualPage;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class VirtualPageConverter implements Converter {

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return VirtualPage.class.isAssignableFrom(type);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		VirtualPage page = (VirtualPage) source;
		ContentPage parent = (ContentPage) page.getParent();
		
		XStreamMarshaller.addReference(context, parent);
		
		writer.addAttribute("parent-id", parent.getId().toString());
		writer.addAttribute("path-component", page.getPathComponent());
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		String parentId = reader.getAttribute("parent-id");
		String pathComponent = reader.getAttribute("path-component");
		
		ContentPage parent = ContentPage.load(Long.valueOf(parentId));
		SystemPageType parentType = (SystemPageType) parent.getPageType();
		VirtualPageType type = parentType.getVirtualChildType();
		Page page = type.resolve(parent, pathComponent);
		XStreamMarshaller.addReference(context, parent);
		return page;
	}

	

}
