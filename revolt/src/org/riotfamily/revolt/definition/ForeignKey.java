package org.riotfamily.revolt.definition;

import java.util.ArrayList;
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

	private List<Reference> references;
	
	private String deleteAction;
	
	private String updateAction;

	public ForeignKey() {
	}
	
	public ForeignKey(String name) {
		super(name);
	}

	public ForeignKey(String name, String foreignTable,
			List<Reference> references, 
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

	public List<Reference> getReferences() {
		return this.references;
	}

	public void setReferences(List<Reference> references) {
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
	
	public List<Identifier> getLocalColumns() {
		List<Identifier> columns = new ArrayList<Identifier>();
		for (Reference reference : references) {
			columns.add(reference.getLocalColumn());
		}
		return columns;
	}

	public List<Identifier> getForeignColumns() {
		List<Identifier> columns = new ArrayList<Identifier>();
		for (Reference reference : references) {
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
