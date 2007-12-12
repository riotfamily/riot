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
package org.riotfamily.revolt.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class ForeignKey extends Identifier {
	
	private String foreignTable;

	private List references;
	
	/* Options: NO ACTION, SET NULL, CASCADE, SET DEFAULT */
	private String deleteOption;
	
	/* Options: NO ACTION, SET NULL, CASCADE, SET DEFAULT */
	private String updateOption;

	public ForeignKey() {
	}
	
	public ForeignKey(String name) {
		super(name);
	}

	public ForeignKey(String name, String foreignTable, List references, String deleteOption, String updateOption) {
		super(name);
		this.foreignTable = foreignTable;
		this.references = references;
		this.deleteOption = deleteOption;
		this.updateOption = updateOption;
	}

	public String getForeignTable() {
		return this.foreignTable;
	}

	public void setForeignTable(String foreignTable) {
		this.foreignTable = foreignTable;
	}

	public List getReferences() {
		return this.references;
	}

	public void setReferences(List references) {
		this.references = references;
	}
	

	public String getDeleteOption() {
		return this.deleteOption;
	}

	public void setDeleteOption(String deleteOption) {
		this.deleteOption = deleteOption;
	}

	public String getUpdateOption() {
		return this.updateOption;
	}

	public void setUpdateOption(String updateOption) {
		this.updateOption = updateOption;
	}
	


	public List getLocalColumns() {
		List columns = new ArrayList();
		Iterator it = references.iterator();
		while (it.hasNext()) {
			Reference reference = (Reference) it.next();
			columns.add(reference.getLocalColumn());
		}
		return columns;
	}

	public List getForeignColumns() {
		List columns = new ArrayList();
		Iterator it = references.iterator();
		while (it.hasNext()) {
			Reference reference = (Reference) it.next();
			columns.add(reference.getForeignColumn());
		}
		return columns;
	}

}
