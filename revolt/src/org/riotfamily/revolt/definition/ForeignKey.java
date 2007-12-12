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
	public static final String CASCADE_HANDLER = "cascade";
	public static final String SET_NULL_HANDLER = "set-null";
	public static final String NO_ACTION_HANDLER = "no-action";
	public static final String SET_DEFAULT_HANDLER = "set-default";
	
	private String foreignTable;

	private List references;
	
	private String deleteAction;
	
	private String updateAction;

	public ForeignKey() {
	}
	
	public ForeignKey(String name) {
		super(name);
	}

	public ForeignKey(String name, String foreignTable, List references,
		String deleteAction, String updateAction) {
		
		super(name);
		this.foreignTable = foreignTable;
		this.references = references;
		this.deleteAction = deleteAction;
		this.updateAction = updateAction;
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
	

	public String getDeleteAction() {
		return this.deleteAction;
	}

	public void setDeleteAction(String deleteAction) {
		this.deleteAction = deleteAction;
	}

	public String getUpdateAction() {
		return this.updateAction;
	}

	public void setUpdateAction(String updateAction) {
		this.updateAction = updateAction;
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

	public boolean hasUpdateAction() {
		return updateAction != null;
	}

	public boolean hasDeleteAction() {
		return deleteAction != null;
	}
}
