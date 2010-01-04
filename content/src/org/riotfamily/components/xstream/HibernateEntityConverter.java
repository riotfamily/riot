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
package org.riotfamily.components.xstream;

import java.io.Serializable;

import javax.persistence.Entity;

import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class HibernateEntityConverter implements Converter {

	private Mapper mapper;
	
	private ApplicationContext applicationContext;
	
	private SessionFactory sessionFactory;
	
	public HibernateEntityConverter(Mapper mapper, ApplicationContext applicationContext) {
		this.mapper = mapper;
		this.applicationContext = applicationContext;
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.isAnnotationPresent(Entity.class);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		XStreamMarshaller.addReference(context, source);
		Serializable id = HibernateUtils.getIdAndSaveIfNecessary(getSessionFactory(), source);
		
		if (id instanceof Long) {
			writer.addAttribute("id", id.toString());
		}
		else {
			String name = mapper.serializedClass(id.getClass());
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, id.getClass());
			context.convertAnother(id);
			writer.endNode();
		}
	}

	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = BeanFactoryUtils.beanOfType(applicationContext, SessionFactory.class);
		}
		return sessionFactory;
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		Class<?> entityClass = mapper.realClass(reader.getNodeName());
		
		Serializable id;
		String s = reader.getAttribute("id");
		if (s != null) {
			id = Long.valueOf(s);
		}
		else {
			reader.moveDown();
	        Class<?> idType = HierarchicalStreams.readClassType(reader, mapper);
	        id = (Serializable) context.convertAnother(null, idType);
	        reader.moveUp();
		}
		Object entity = getSessionFactory().getCurrentSession().load(entityClass, id);
		XStreamMarshaller.addReference(context, entity);
		return entity;
	}

}
