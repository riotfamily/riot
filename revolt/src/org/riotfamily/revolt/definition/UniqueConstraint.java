package org.riotfamily.revolt.definition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class UniqueConstraint extends Identifier {

	private Collection<Identifier> columns;

	public UniqueConstraint() {
	}

	public UniqueConstraint(String name) {
		super(name);
	}
	
	public UniqueConstraint(String name, String[] columnNames) {
		super(name);
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

}
