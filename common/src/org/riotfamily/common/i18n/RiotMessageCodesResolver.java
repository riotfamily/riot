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
package org.riotfamily.common.i18n;

import java.util.ArrayList;

import org.riotfamily.common.util.PropertyUtils;
import org.springframework.util.StringUtils;



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
	
	public String[] resolveLabel(String objectName, Class objectClass) {
		ArrayList codes = new ArrayList(2);
		if (objectName != null) {
			codes.add(objectName);
		}
		if (objectClass != null) {
			codes.add(objectClass.getName());
		}
		return StringUtils.toStringArray(codes);
	}

	public String[] resolveLabel(String objectName, Class objectClass, 
			String field) {
		
		ArrayList codes = new ArrayList(2);
		if (objectName != null) {
			codes.add(objectName + '.' + field);
		}
		if (objectClass != null) {
			codes.add(PropertyUtils.getDeclaringClass(
					objectClass, field).getName() + '.' + field);
		}
		return StringUtils.toStringArray(codes);
	}
	
	public String[] resolveHint(String objectName, Class objectClass, 
			String field) {
		
		ArrayList codes = new ArrayList(2);
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
