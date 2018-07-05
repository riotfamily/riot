
package org.riotfamily.common.hibernate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.type.Type;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;

public class FilterDefinitionFactoryBean  implements FactoryBean<FilterDefinition>, BeanNameAware, InitializingBean {

	private static Method heuristicTypeMethod;

	private static Object typeResolver;

	static {
		try {
			Class<?> trClass = FilterDefinitionFactoryBean.class.getClassLoader().loadClass(
					"org.hibernate.type.TypeResolver");
			heuristicTypeMethod = trClass.getMethod("heuristicType", String.class);
			typeResolver = trClass.newInstance();
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot find Hibernate's heuristicType method", e);
		}
	}


	private String filterName;

	private Map<String, Type> parameterTypeMap = new HashMap<String, Type>();

	private String defaultFilterCondition;

	private FilterDefinition filterDefinition;


	/**
	 * Set the name of the filter.
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * Set the parameter types for the filter,
	 * with parameter names as keys and type names as values.
	 * @see org.hibernate.type.TypeFactory#heuristicType(String)
	 */
	public void setParameterTypes(Map<String, String> parameterTypes) {
		if (parameterTypes != null) {
			this.parameterTypeMap = new HashMap<String, Type>(parameterTypes.size());
			for (Map.Entry<String, String> entry : parameterTypes.entrySet()) {
				this.parameterTypeMap.put(entry.getKey(),
						(Type) ReflectionUtils.invokeMethod(heuristicTypeMethod, typeResolver, entry.getValue()));
			}
		}
		else {
			this.parameterTypeMap = new HashMap<String, Type>();
		}
	}

	/**
	 * Specify a default filter condition for the filter, if any.
	 */
	public void setDefaultFilterCondition(String defaultFilterCondition) {
		this.defaultFilterCondition = defaultFilterCondition;
	}

	/**
	 * If no explicit filter name has been specified, the bean name of
	 * the FilterDefinitionFactoryBean will be used.
	 * @see #setFilterName
	 */
	public void setBeanName(String name) {
		if (this.filterName == null) {
			this.filterName = name;
		}
	}

	public void afterPropertiesSet() {
		this.filterDefinition =
				new FilterDefinition(this.filterName, this.defaultFilterCondition, this.parameterTypeMap);
	}


	public FilterDefinition getObject() {
		return this.filterDefinition;
	}

	public Class<FilterDefinition> getObjectType() {
		return FilterDefinition.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
