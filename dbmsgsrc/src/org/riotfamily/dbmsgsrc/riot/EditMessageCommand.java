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
package org.riotfamily.dbmsgsrc.riot;

import java.util.Locale;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.dialog.DialogCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.Textarea;
import org.riotfamily.pages.model.Site;

public class EditMessageCommand extends DialogCommand {

	@Override
	protected String getIcon() {
		return "pencil";
	}
	
	@Override
	protected String getName() {
		return "edit";
	}
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		return selection.size() == 1;
	}
	
	@Override
	protected boolean isShowOnForm(CommandContext context) {
		return false;
	}
	
	@Override
	public Form createForm(CommandContext context, Selection selection) {
		Form form = new Form(Message.class);

		Message message = (Message) selection.getSingleItem().getObject();
		if (MessageBundleEntry.C_LOCALE.equals(message.getLocale())) {
			MessageBundleEntry entry = message.getEntry();
			Site site = (Site) context.getParent();
			Locale locale = site.getLocale();
			entry.addTranslation(locale);
			message = entry.getMessages().get(locale);
			message.setEntry(entry);
		}
		
		form.setBackingObject(message);
		Textarea comment = new Textarea();
		comment.setReadOnly(true);
		Textarea text = new Textarea();
		form.addElement(comment, "entry.comment");
		form.addElement(text, "text");
		addButton(form, "Save");
		return form;
	}
	
	@Override
	public CommandResult handleInput(CommandContext context,
			Selection selection, Object input, String button) {

		context.getScreenContext().getDao().update(input);
		return new RefreshListResult();
	}

}