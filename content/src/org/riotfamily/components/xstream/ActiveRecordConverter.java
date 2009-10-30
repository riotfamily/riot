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

import java.io.Serializable;

import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.common.hibernate.ActiveRecordUtils;
import org.riotfamily.common.hibernate.Persistable;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ActiveRecordConverter implements Converter {

	private Mapper mapper;
	
	public ActiveRecordConverter(Mapper mapper) {
		this.mapper = mapper;
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return Persistable.class.isAssignableFrom(type);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		Persistable record = (Persistable) source;
		XStreamMarshaller.addReference(context, record);
		Serializable id = ActiveRecordUtils.getIdAndSaveIfNecessary(record);
		if (id instanceof Long) {
			writer.addAttribute("id", id.toString());
		}
		else {
			String name = mapper.serializedClass(id.getClass());
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, id.getClass());
			context.convertAnother(id);
			writer.endNode();
		}
	}

	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		Class<? extends ActiveRecord> recordType =
				mapper.realClass(reader.getNodeName());
		
		Serializable id;
		String s = reader.getAttribute("id");
		if (s != null) {
			id = Long.valueOf(s);
		}
		else {
			reader.moveDown();
	        Class<?> idType = HierarchicalStreams.readClassType(reader, mapper);
	        id = (Serializable) context.convertAnother(null, idType);
	        reader.moveUp();
		}
		ActiveRecord record = ActiveRecordUtils.load(recordType, id);
		XStreamMarshaller.addReference(context, record);
		return record;
	}

}
