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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.model;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentFactoryService {

	private static ContentFactory contentFactory = new BuiltInContentFactory();
	
	public static void setContentFactory(ContentFactory contentFactory) {
		ContentFactoryService.contentFactory = contentFactory;
	}
	
	public static Content createOrUpdateContent(Content content, Object value) 
			throws ContentCreationException {
		
		if (content != null) {
			try {
				content.setValue(value);
				return content;
			}
			catch (ClassCastException e) {
			}
		}
		return createContent(value);
	}
	
	public static Content createContent(Object value) 
			throws ContentCreationException {
	
		Content content = contentFactory.createContent(value);
		if (content == null) {
			throw new ContentCreationException(
					"ContentFactory returned null for value: " + value);
		}
		return content;
	}
}
