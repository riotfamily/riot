package org.riotfamily.pages.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

/**
 * ParameterResolver that converts a String value to primitive wrapper object. 
 */
public class StringToPrimitiveResolver extends DefaultParameterResolver {

	private static final int TYPE_INTEGER = 1;
	
	private static final int TYPE_LONG = 2;
	
	private static final int TYPE_SHORT = 3;
	
	private static final int TYPE_DOUBLE = 4;
	
	private static final int TYPE_FLOAT = 5;
	
	private static final int TYPE_BOOLEAN = 6;
	
	private static final int TYPE_CHARACTER = 7;
		
	private int type;
	
	
	public void setType(String type) {
		if (type.equalsIgnoreCase("Integer")) {
			this.type = TYPE_INTEGER;
		}
		else if (type.equalsIgnoreCase("Long")) {
			this.type = TYPE_LONG;
		}
		else if (type.equalsIgnoreCase("Short")) {
			this.type = TYPE_SHORT;
		}
		else if (type.equalsIgnoreCase("Double")) {
			this.type = TYPE_DOUBLE;
		}
		else if (type.equalsIgnoreCase("Float")) {
			this.type = TYPE_FLOAT;
		}
		else if (type.equalsIgnoreCase("Boolean")) {
			this.type = TYPE_BOOLEAN;
		}
		else if (type.equalsIgnoreCase("Character")) {
			this.type = TYPE_CHARACTER;
		}
		else {
			throw new IllegalArgumentException("type must be Integer, " +
					"Long, Short, Double, Float, Boolean or Character");
		}
	}


	public Object getValueInternal(HttpServletRequest request) {
		String s = (String) super.getValueInternal(request);
		if (s == null) {
			return null;
		}
		switch (type) {
		case TYPE_INTEGER: 
			return Integer.valueOf(s);
			
		case TYPE_LONG:	
			return Long.valueOf(s);

		case TYPE_SHORT:
			return Short.valueOf(s);
			
		case TYPE_DOUBLE:
			return Double.valueOf(s);
			
		case TYPE_FLOAT:
			return Float.valueOf(s);

		case TYPE_BOOLEAN:
			return Boolean.valueOf(s);
			
		case TYPE_CHARACTER:
			return new Character(s.charAt(0));
			
		default:
			return s;
		}
	}
	
}
