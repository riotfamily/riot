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
package org.riotfamily.riot.list.command.core;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.riot.editor.AbstractDisplayDefinition;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.IntermediateDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.springframework.util.Assert;


/**
 *
 */
public class StepIntoCommand extends AbstractCommand {
	
	public boolean isEnabled(RenderContext context) {
		return getTargetUrl(context) != null;
	}
	
	public CommandResult execute(CommandContext context) {
		return new GotoUrlResult(getTargetUrl(context));
	}
	
	private static String getTargetUrl(CommandContext context) {
		EditorDefinition editorDef = context.getEditorDefinition();
		Assert.notNull(editorDef, "An EditorDefinition must be set");
		
		if (editorDef instanceof IntermediateDefinition) {
			ListDefinition listDef = ((IntermediateDefinition) editorDef).getNestedListDefinition();
			return listDef.getEditorUrl(null, context.getObjectId());
		}
		else {
			Assert.isInstanceOf(AbstractDisplayDefinition.class, editorDef);
			AbstractDisplayDefinition displayDef = (AbstractDisplayDefinition) editorDef; 
			
			List childRefs = displayDef.getChildEditorReferences(
					context.getItem(), context.getMessageResolver());	

			Iterator it = childRefs.iterator();
			while (it.hasNext()) {
				EditorReference ref = (EditorReference) it.next(); 
				return ref.getEditorUrl();
			}
			return null;
		}
	}

}
