package org.riotfamily.core.dao;


/**
 * Exception that can be thrown by a RiotDao to indicate that a property 
 * contains an invalid value. This allows the DAO layer to perform validation
 * checks upon save or update operations. 
 * 
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class InvalidPropertyValueException extends RiotDaoException {

	private String field;

	public InvalidPropertyValueException(String field, String code, String msg) {
		this(field, code, new Object[] {}, msg);
	}

	public InvalidPropertyValueException(String field, String code, Object[] arguments, String msg) {
		this(field, code, arguments, msg, null);
	}

	public InvalidPropertyValueException(String field, String code,
			Object[] arguments, String msg, Throwable cause) {

		super(code, arguments, msg, cause);
		this.field = field;
	}

	public String getField() {
		return this.field;
	}

}
