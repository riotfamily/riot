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

import java.util.ArrayList;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.springframework.util.StringUtils;

/**
 * AdvancedMessageCodesResolver implementation used by Riot.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class RiotMessageCodesResolver implements AdvancedMessageCodesResolver {

	private static final char SEPARATOR = '.';

	private static final String ERROR_PREFIX = "error.";
	
	private static final String HINT_SUFFIX = ".hint";
	
	public String[] resolveMessageCodes(String errorCode, String objectName) {
		if (errorCode.startsWith(ERROR_PREFIX)) {
			return new String[] {
					errorCode
			};
		} 
		else {
			return new String[] {
				ERROR_PREFIX + objectName + SEPARATOR + errorCode,
				ERROR_PREFIX + errorCode
			};
		}
	}

	@SuppressWarnings("unchecked")
	public String[] resolveMessageCodes(String errorCode, String objectName, 
			String field, Class fieldType) {
		
		if (errorCode.startsWith(ERROR_PREFIX)) {
			return new String[] {
					errorCode
			};
		} 
		else {
			return new String[] {
				ERROR_PREFIX + objectName + SEPARATOR + field + SEPARATOR + errorCode,
				ERROR_PREFIX + field + SEPARATOR + errorCode,
				ERROR_PREFIX + errorCode
			};
		}
	}
	
	public String[] resolveLabel(String objectName, Class<?> objectClass) {
		ArrayList<String> codes = new ArrayList<String>(2);
		if (objectName != null) {
			codes.add(objectName);
		}
		if (objectClass != null) {
			codes.add(objectClass.getName());
		}
		return StringUtils.toStringArray(codes);
	}

	public String[] resolveLabel(String objectName, Class<?> objectClass, 
			String field) {
		
		ArrayList<String> codes = new ArrayList<String>(2);
		if (objectName != null) {
			codes.add(objectName + '.' + field);
		}
		if (objectClass != null) {
			codes.add(PropertyUtils.getDeclaringClass(
					objectClass, field).getName() + '.' + field);
		}
		return StringUtils.toStringArray(codes);
	}
	
	public String[] resolveHint(String objectName, Class<?> objectClass, 
			String field) {
		
		ArrayList<String> codes = new ArrayList<String>(2);
		if (field == null) {
			if (objectName != null) {
				codes.add(objectName +  HINT_SUFFIX);
			}
			if (objectClass != null) {
				codes.add(PropertyUtils.getDeclaringClass(
						objectClass, field).getName() + HINT_SUFFIX);
			}
		}
		else {
			if (objectName != null) {
				codes.add(objectName +  '.' + field + HINT_SUFFIX);
			}
			if (objectClass != null) {
				codes.add(PropertyUtils.getDeclaringClass(
						objectClass, field).getName() + '.' + field + HINT_SUFFIX);
			}
		}
		return StringUtils.toStringArray(codes);
	}

}
