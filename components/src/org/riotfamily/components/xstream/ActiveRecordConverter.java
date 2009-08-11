package org.riotfamily.components.xstream;

import java.io.Serializable;

import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.common.hibernate.ActiveRecordUtils;

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
		return ActiveRecord.class.isAssignableFrom(type);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		ActiveRecord record = (ActiveRecord) source;
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
