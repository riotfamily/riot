package org.riotfamily.common.i18n;

import org.riotfamily.common.util.PropertyUtils;



public class RiotMessageCodesResolver implements AdvancedMessageCodesResolver {

	private static final char SEPARATOR = '.';

	private static final String ERROR_PREFIX = "error.";
	
	private static final String HINT_SUFFIX = ".hint";
	
	public String[] resolveMessageCodes(String errorCode, String objectName) {
		if (errorCode.startsWith(ERROR_PREFIX)) {
			return new String[] {
					errorCode
			};
		} else {
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
		} else {
			return new String[] {
				ERROR_PREFIX + objectName + SEPARATOR + field + SEPARATOR + errorCode,
				ERROR_PREFIX + field + SEPARATOR + errorCode,
				ERROR_PREFIX + errorCode
			};
		}
	}
	
	public String[] resolveLabel(String objectName, Class objectClass) {
		return new String[] {
			objectName,
			objectClass.getName()
		};
	}

	public String[] resolveLabel(String objectName, Class objectClass, String field) {
		return new String[] {
			objectName + '.' + field,
			PropertyUtils.getDeclaringClass(objectClass, field).getName() + '.' + field
		};
	}
	
	public String[] resolveHint(String objectName, Class objectClass, String field) {
		if (field == null) {
			return new String[] {
					objectName +  HINT_SUFFIX ,
					PropertyUtils.getDeclaringClass(objectClass, field).getName() + HINT_SUFFIX
				};
		}
		else {
			return new String[] {
				objectName +  '.' + field + HINT_SUFFIX ,
				PropertyUtils.getDeclaringClass(objectClass, field).getName() + '.' + field + HINT_SUFFIX
			};
		}
	}

}
