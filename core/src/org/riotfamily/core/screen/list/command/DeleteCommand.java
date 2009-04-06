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
package org.riotfamily.core.screen.list.command;

import java.util.HashMap;

import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;

public class DeleteCommand extends DialogCommand {

	public boolean isEnabled(CommandContext context, Selection selection) {
		return selection.size() > 0;
	}
	
	@Override
	public Form createForm(Selection selection) {
		Form form = new Form(HashMap.class);
		TextField txt = new TextField();
		txt.setRequired(true);
		form.addElement(txt, "text");
		//form.addButton("ok");
		//form.addButton("cancel");
		Button ok = new Button();
		ok.setLabel("Ok");
		ok.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				Form f = event.getSource().getForm();
				if (!f.hasErrors()) {
					f.getFormListener().eval(
						"riot.window.closeAll(); list.handleInput('" + "foo" + "');"
					);
				}	
			}
		});
		ok.setPartitialSubmit(form.getId());
		form.addButton(ok);
		return form;
	}
	
	@Override
	public CommandResult handleInput(CommandContext context,
			Selection selection, Object input, String button) {
	
		return new NotificationResult("Input: " + input + " Button: " + button);
	}

}
