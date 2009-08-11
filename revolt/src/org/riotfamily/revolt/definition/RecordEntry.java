package org.riotfamily.revolt.definition;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class RecordEntry extends Identifier {

	private String value;

	public RecordEntry(String name, String value) {
		super(name);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
