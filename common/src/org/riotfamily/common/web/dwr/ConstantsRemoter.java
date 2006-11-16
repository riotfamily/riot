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
package org.riotfamily.common.web.dwr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.directwebremoting.extend.Creator;
import org.directwebremoting.impl.DefaultRemoter;

public class ConstantsRemoter extends DefaultRemoter {

	
	public String generateInterfaceScript(String scriptName, String path) 
			throws SecurityException {
		
		StringBuffer buffer = new StringBuffer();
		Creator creator = creatorManager.getCreator(scriptName);
		Field[] fields = creator.getType().getFields();
		for (int i = 0; i < fields.length; i++) {
			int mod = fields[i].getModifiers();
			String name = fields[i].getName(); 
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod) 
					&& Modifier.isFinal(mod) 
					&& name.equals(name.toUpperCase())) {
				
				try {
					//TODO We should consider using a ConverterManager to convert the value
					Object value = fields[i].get(null);
					buffer.append(scriptName).append('.').append(name)
							.append(" = ").append(value).append(";\n"); 
				}
				catch (IllegalAccessException e) {
				}
			}
		}
		
		buffer.append(super.generateInterfaceScript(scriptName, path));
		
		return buffer.toString();
	}
	
}
