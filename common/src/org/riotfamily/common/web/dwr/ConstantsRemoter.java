package org.riotfamily.common.web.dwr;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.directwebremoting.Creator;
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
