package org.riotfamily.core.dao;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataAccessException;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class RiotDaoException extends DataAccessException
		implements MessageSourceResolvable {

	private String code;

	private Object[] arguments;

	public RiotDaoException(String code, String msg) {
		this(code, new Object[] {}, msg);
	}

	public RiotDaoException(String code, Object[] arguments, String msg) {
		this(code, arguments, msg, null);
	}

	public RiotDaoException(String code, Object[] arguments, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
		this.arguments = arguments;
	}

	public Object[] getArguments() {
		return this.arguments;
	}

	public String getCode() {
		return this.code;
	}

	public String[] getCodes() {
		return new String[] { code };
	}

	public String getDefaultMessage() {
		return getMessage();
	}

}
