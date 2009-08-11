package org.riotfamily.components.model;


public interface ContentMapMarshaller {

	public ContentMap unmarshal(Content owner, String xml);
	
	public String marshal(ContentMap contentMap);

}
