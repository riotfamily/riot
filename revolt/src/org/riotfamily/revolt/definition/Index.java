package org.riotfamily.revolt.definition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Index extends Identifier {

	private Collection<Identifier> columns;

	private boolean unique;

	public Index() {
	}

	public Index(String name) {
		super(name);
	}
			
	public Index(String name, String[] columnNames, boolean unique) {
		super(name);
		this.unique = unique;
		setColumnNames(columnNames);
	}
	
	public Collection<Identifier> getColumns() {
		return this.columns;
	}

	public void setColumnNames(String[] names) {
		columns = new ArrayList<Identifier>();
		for (int i = 0; i < names.length; i++) {
			columns.add(new Identifier(names[i]));
		}
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
}
