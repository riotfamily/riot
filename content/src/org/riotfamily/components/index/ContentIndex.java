/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.components.index;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.riotfamily.components.model.Content;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract superclass for all ContentIndex entities. All fields defined by 
 * subclasses will be populated from the Content being indexed.
 * See {@link #index(Content)} for details.
 * <p>
 * <b>Important:</b> In order to work, subclasses must have the following annotations:
 * <pre>
 * {@literal @}Entity
 * {@literal @}Filter(name="contentIndex")
 * </pre>
 */
@MappedSuperclass
//@Filter(name="contentIndex")
// Defining filters on a MappedSuperclass is currently not supported by Hibernate.
// see http://opensource.atlassian.com/projects/hibernate/browse/HHH-4332
public abstract class ContentIndex {

	@Id @SuppressWarnings("unused")
	private Long id;
	
	@OneToOne
	@PrimaryKeyJoinColumn
	private Content content;
	
	private boolean live;
	
	public boolean isLive() {
		return live;
	}
	
	public Content getContent() {
		return content;
	}
	
	public final void setContent(Content content) {
		this.content = content;
		this.live = content == content.getContainer().getLiveVersion();
		index(content);
	}

	/**
	 * Loops over all fields and populates them with values from the given Content.
	 * Fields that already have a non-null value are ignored. For all other fields
	 * {@link #getContentValue(String, Content)} is invoked.
	 */
	protected void index(Content content) {
		for (Field field : getClass().getDeclaredFields()) {
			ReflectionUtils.makeAccessible(field);
			if (ReflectionUtils.getField(field, this) == null) {
				Object value = getContentValue(field.getName(), content);
				if (value != null && !field.getType().isInstance(value)) {
					throw new IllegalArgumentException(String.format(
							"Field %s is of type %s but content value is a %s",
							field.getName(), field.getType(), value.getClass()));
				}
				ReflectionUtils.setField(field, this, value);
			}
		}
	}

	/**
	 * Returns the content value for the given field name. If a method with 
	 * the signature <code>Object get&lt;Name&gt;(Content)</code> exists, it
	 * is invoked. Otherwise {@link Content#get(Object) content.get(name)} is
	 * called.
	 */
	protected Object getContentValue(String name, Content content) {
		Method method = ReflectionUtils.findMethod(getClass(), "get" 
				+ StringUtils.capitalize(name), Content.class);
		
		if (method != null) {
			return ReflectionUtils.invokeMethod(method, this, content);
		}
		return content.get(name);
	}
	
}
