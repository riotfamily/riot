/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
