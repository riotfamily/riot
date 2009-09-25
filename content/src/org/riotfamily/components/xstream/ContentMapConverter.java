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

import java.util.Map;

import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.model.ContentMapImpl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ContentMapConverter extends AbstractCollectionConverter {
	
	public ContentMapConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return ContentMap.class.isAssignableFrom(type);
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, 
			MarshallingContext context) {
		
		ContentMap map = (ContentMap) source;
		writer.addAttribute("id", map.getFragmentId());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writer.startNode("entry");
            writer.addAttribute("key", entry.getKey());
            writeItem(entry.getValue(), context, writer);
            writer.endNode();
        }
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, 
			UnmarshallingContext context) {
		
		Content owner = (Content) context.get("content");
		String id = reader.getAttribute("id");
		ContentMap map = new ContentMapImpl(owner, id);
		populateMap(reader, context, map);
        return map;
    }

	protected void populateMap(HierarchicalStreamReader reader,
			UnmarshallingContext context, ContentMap map) {
		
		while (reader.hasMoreChildren()) {
            reader.moveDown();
            String key = reader.getAttribute("key");

            String parentPath = (String) context.get("path");
    		String path = parentPath != null ? parentPath + " " + key : key;
    		context.put("path", path);
    		
            reader.moveDown();
            map.put(key, readItem(reader, context, map));
            reader.moveUp();

            context.put("path", parentPath);

            reader.moveUp();
        }
	}
	
}
