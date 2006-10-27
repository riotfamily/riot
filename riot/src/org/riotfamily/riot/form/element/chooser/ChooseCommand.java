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
package org.riotfamily.riot.form.element.chooser;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ScriptResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;

public class ChooseCommand extends AbstractCommand {

	private static final String DEFAULT_ID = "choose";
	
	public ChooseCommand() {
		setId(DEFAULT_ID);
	}
	
	public boolean isEnabled(RenderContext context) {
		HttpServletRequest request = context.getRequest();
		EditorDefinition target = (EditorDefinition) request.getAttribute(
				ChooserListController.TARGET_EDITOR_ATTR);
		
		ListDefinition targetList = EditorDefinitionUtils.getListDefinition(target);
		
		//FIXME This is kind of ugly:
		if (targetList instanceof TreeDefinition) {
			targetList = ((TreeDefinition) targetList).getNodeListDefinition();
		}
		
		if (context.getListDefinition().equals(targetList)) {
			return target.getBeanClass().isInstance(context.getItem());
		}
		return false;
	}
	
	public CommandResult execute(CommandContext context) {
		return new ScriptResult("parent.chosen('" + 
				context.getObjectId() + "')");
	}

}
