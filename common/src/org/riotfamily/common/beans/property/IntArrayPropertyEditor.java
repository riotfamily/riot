package org.riotfamily.common.beans.property;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class IntArrayPropertyEditor extends PropertyEditorSupport {

	public String getAsText() {
		Integer[] array = (Integer[]) getValue();
		return StringUtils.arrayToCommaDelimitedString(array);
	}

	public void setAsText(String text) throws IllegalArgumentException {
		String[] s = StringUtils.commaDelimitedListToStringArray(text);
		Integer[] array = new Integer[s.length];
		for (int i = 0; i < s.length; i++) {
			array[i] = Integer.valueOf(s[i]);
		}
		setValue(array);
	}
}
