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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.i18n;

import org.springframework.validation.MessageCodesResolver;

/**
 * Extension of the Spring {@link MessageCodesResolver} interface that provides
 * methods to resolve codes for labels and hints.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface AdvancedMessageCodesResolver extends MessageCodesResolver {

	String[] resolveLabel(String objectName, Class<?> objectClass);
	
	String[] resolveLabel(String objectName, Class<?> objectClass, String field);
	
	String[] resolveHint(String objectName, Class<?> objectClass, String field);
	
}
