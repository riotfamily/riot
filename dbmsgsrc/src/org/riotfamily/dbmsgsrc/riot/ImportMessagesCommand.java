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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.dbmsgsrc.riot;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.upload.FileUpload;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.dialog.DialogCommand;
import org.springframework.web.servlet.ModelAndView;

public class ImportMessagesCommand extends DialogCommand {

	public static final String ACTION_IMPORT = "import";
	
	@Override
	protected String getAction(CommandContext context) {
		return ACTION_IMPORT;
	}
	
	@Override
	public Form createForm(Object bean) {
		Form form = new Form(Upload.class);
		FileUpload fileUpload = new FileUpload();
		fileUpload.setRequired(true);
		form.addElement(fileUpload, "data");
		return form;
	}
	
	@Override
	public ModelAndView handleInput(Object input) {
		Upload upload = (Upload) input;
		upload.getData();
		return null;
	}
	
	public static class Upload {
		
		private byte[] data;

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}
		
	}
}
