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
package org.riotfamily.common.i18n;

import java.util.List;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.util.StringUtils;

/**
 * AdvancedMessageCodesResolver implementation used by Riot.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class RiotMessageCodesResolver implements AdvancedMessageCodesResolver {

	/**
	 * error.<objectName>.<code>
	 * error.<code>
	 */
	public String[] resolveMessageCodes(String errorCode, String objectName) {
		if (errorCode == null) {
			return null;
		}
		if (errorCode.startsWith("error.")) {
			return new String[] {
					errorCode
			};
		} 
		else {
			return new String[] {
				"error." + objectName + '.' + errorCode,
				"error." + errorCode
			};
		}
	}

	/**
	 * error.<objectName>.<field>.<code>
	 * error.<field>.<code>
	 * error.<code>
	 */
	@SuppressWarnings("unchecked")
	public String[] resolveMessageCodes(String errorCode, String objectName, 
			String field, Class fieldType) {
		
		if (errorCode == null) {
			return null;
		}
		if (errorCode.startsWith("error.")) {
			return new String[] {
					errorCode
			};
		} 
		else {
			return new String[] {
				"error." + objectName + '.' + field + '.' + errorCode,
				"error." + field + '.' + errorCode,
				"error." + errorCode
			};
		}
	}
	
	/**
	 * <objectName>
	 * <objectClass>
	 */
	public String[] resolveLabelCodes(String objectName, Class<?> objectClass) {
		List<String> codes = Generics.newArrayList(2);
		if (objectName != null) {
			codes.add(objectName);
		}
		if (objectClass != null) {
			codes.add(objectClass.getName());
		}
		return StringUtils.toStringArray(codes);
	}

	/**
	 * <objectName>.<field>
	 * <declaringClass>.<field>
	 */
	public String[] resolveLabelCodes(String objectName, Class<?> objectClass, 
			String field) {
		
		List<String> codes = Generics.newArrayList(2);
		if (objectName != null) {
			codes.add(objectName + '.' + field);
		}
		if (objectClass != null) {
			codes.add(PropertyUtils.getDeclaringClass(
					objectClass, field).getName() + '.' + field);
		}
		return StringUtils.toStringArray(codes);
	}
	
	/**
	 * <objectName>.<code>
	 * <objectClass>.<code>
	 * <elementName>.<code>
	 */
	public String[] resolveUICodes(String objectName, Class<?> objectClass, 
			String elementName, String code) {
		
		List<String> codes = Generics.newArrayList(3);
		if (objectName != null) {
			codes.add(objectName + '.' + code);
		}
		if (objectClass != null) {
			codes.add(objectClass.getName() + '.' + code);
		}
		if (elementName != null) {
			codes.add(elementName + '.' + code);
		}
		return StringUtils.toStringArray(codes);
	}

	
	/**
	 * <objectName>.<field>.<code>
	 * <delcaringClass>.<field>.<code>
	 * label.<elementName>.<code>
	 */
	public String[] resolveUICodes(String objectName, Class<?> objectClass, 
			String field, String elementName, String code) {
		
		List<String> codes = Generics.newArrayList(3);
		if (objectName != null) {
			codes.add(objectName +  '.' + field + '.' + code);
		}
		if (objectClass != null) {
			String declaringClassName = PropertyUtils.getDeclaringClass(objectClass, field).getName();
			codes.add(declaringClassName + '.' + field + '.' + code);
		}
		if (elementName != null) {
			codes.add("label." + elementName + '.' + code);
		}
		return StringUtils.toStringArray(codes);
	}

}
