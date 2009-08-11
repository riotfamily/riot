package org.riotfamily.revolt.definition;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class Reference {

	private Identifier localColumn;

	private Identifier foreignColumn;
	
	public Reference(String localColumn, String foreignColumn) {
		this.localColumn = new Identifier(localColumn);
		this.foreignColumn = new Identifier(foreignColumn);
	}

	public Identifier getForeignColumn() {
		return this.foreignColumn;
	}

	public Identifier getLocalColumn() {
		return this.localColumn;
	}

}
