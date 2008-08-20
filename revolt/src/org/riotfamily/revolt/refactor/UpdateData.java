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
package org.riotfamily.revolt.refactor;

import java.util.Collection;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.EvolutionException;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.UpdateStatement;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class UpdateData implements Refactoring {

	private Collection<UpdateStatement> statements;
	
	public UpdateData(Collection<UpdateStatement> statements) {
		this.statements = statements;
	}

	public Script getScript(Dialect dialect) {
		Script result = new Script();
		for (UpdateStatement statement : statements) {
			if (statement.supports(dialect)) {
				result.append(new Script(statement.getSql()));				
			}
			else {
				throw new EvolutionException("No update statment for dialect " 
						+ dialect.getName());
			}
		}
		return result;		
	}
	
}
