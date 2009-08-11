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
		Component component = new Component(list, id);
		component.setType(reader.getAttribute("type"));
        populateMap(reader, context, component);
        return component;
    }
}
