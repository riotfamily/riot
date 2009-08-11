package org.riotfamily.revolt;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;



/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public interface Refactoring {

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) throws Exception;

}
