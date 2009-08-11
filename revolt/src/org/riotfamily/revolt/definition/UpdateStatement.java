package org.riotfamily.revolt.definition;

import java.util.Set;

import org.riotfamily.revolt.Dialect;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class UpdateStatement {

	private Set<String> supportedDialectNames;
	
	private String sql;

	@SuppressWarnings("unchecked")
	public UpdateStatement(String dialects, String sql) {
		if (StringUtils.hasText(dialects)) {
			this.supportedDialectNames = StringUtils.commaDelimitedListToSet(
					dialects.toLowerCase());
		}
		this.sql = sql;
	}
	
	public boolean supports(Dialect dialect) {
		return supportedDialectNames == null || supportedDialectNames.contains(
				dialect.getName().toLowerCase());
	}
	
	public String getSql() {
		return sql;
	}

}
