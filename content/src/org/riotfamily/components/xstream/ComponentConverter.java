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

import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ComponentConverter extends ContentMapConverter {

	public ComponentConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return Component.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, 
			MarshallingContext context) {
		
		Component component = (Component) source;
		writer.addAttribute("type", component.getType());
		super.marshal(component, writer, context);
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, 
			UnmarshallingContext context) {
		
		ComponentList list = (ComponentList) context.get("list");
		String id = reader.getAttribute("id");
		String type = reader.getAttribute("type");
		
		String parentPath = (String) context.get("path");
		String path = parentPath + " " + type;
		context.put("path", path);
		
		Component component = new Component(list, id);
		component.setType(type);
        populateMap(reader, context, component);
        
        context.put("path", parentPath);
        return component;
    }
}
