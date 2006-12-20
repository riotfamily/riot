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
package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

public class MessageRenderer implements CellRenderer {
	
	public void render(String propertyName, Object value, RenderContext context, PrintWriter writer) {
		if (value != null) {
			StringBuffer key = new StringBuffer();
			key.append(context.getListId());
			key.append('.');
			key.append(propertyName);
			
			if (value instanceof String) {
				key.append('.').append(value);
			}
		
			writer.print(context.getMessageResolver().getMessage(key.toString(), 
					new Object[] { value }, value.toString()));
		}
	}

	

}
