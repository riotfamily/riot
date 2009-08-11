package org.riotfamily.components.xstream;

import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ComponentListConverter extends CollectionConverter {
	
	public ComponentListConverter(Mapper mapper) {
		super(mapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return ComponentList.class.isAssignableFrom(type);
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, 
			MarshallingContext context) {
		
		ComponentList list = (ComponentList) source;
		writer.addAttribute("id", list.getPartId());
		super.marshal(list, writer, context);
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, 
			UnmarshallingContext context) {
		
		Content owner = (Content) context.get("content");
		String id = reader.getAttribute("id");
		ComponentList list = new ComponentList(owner, id);
		ComponentList parentList = (ComponentList) context.get("list");
		context.put("list", list);
        populateCollection(reader, context, list);
        context.put("list", parentList);
        return list;
    }

}
