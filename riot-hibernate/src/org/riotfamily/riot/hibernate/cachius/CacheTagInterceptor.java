package org.riotfamily.riot.hibernate.cachius;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.SessionFactory;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.hibernate.SessionFactoryAwareInterceptor;
import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.riotfamily.website.cache.CacheTagUtils;
import org.riotfamily.website.cache.TagCacheItems;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Hibernate Interceptor that invalidates tagged cache items whenever an entity 
 * with a {@link TagCacheItems} annotation is modified or deleted.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CacheTagInterceptor extends EmptyInterceptor 
		implements SessionFactoryAwareInterceptor {

	private CacheService cacheService;
	
	private SessionFactory sessionFactory;
	
	private Map<Class<?>, List<Field>> inverseMappingFields = Generics.newHashMap();
	
	public CacheTagInterceptor(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.tag(entity.getClass(), id);
		}
		return false;
	}		
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.invalidate(cacheService, entity.getClass(), id);
		}
		invalidateOwners(entity);
	}
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.invalidate(cacheService, entity.getClass());
		}
		invalidateOwners(entity);
		return false;
	}
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
	
		if (entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
			CacheTagUtils.invalidate(cacheService, entity.getClass(), id);
		}
		invalidateOwners(entity);
		return false;
	}
	
	@Override
	public void onCollectionUpdate(Object collection, Serializable key)
			throws CallbackException {
		
		if (collection instanceof PersistentCollection) {
			PersistentCollection pc = (PersistentCollection) collection;
			Object entity = pc.getOwner();
			if (entity != null && entity.getClass().isAnnotationPresent(TagCacheItems.class)) {
				CacheTagUtils.invalidate(cacheService, entity.getClass(), pc.getKey());		
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void invalidateOwners(Object entity) {
		List<Field> fields = getInverseMappingFields(entity.getClass());
		if (fields != null) {
			for (Field field : fields) {
				try {
					Object owner = field.get(entity);
					Serializable id = HibernateUtils.getId(sessionFactory, owner);
					CacheTagUtils.invalidate(cacheService, field.getType(), id);
				}
				catch (IllegalArgumentException e) {
				}
				catch (IllegalAccessException e) {
				}
			}
		}
	}
	
	private List<Field> getInverseMappingFields(Class<?> clazz) {
		List<Field> fields = inverseMappingFields.get(clazz);
		if (fields == null) {
			fields = findInverseMappingFields(clazz);
			inverseMappingFields.put(clazz, fields);
		}
		return fields;
	}
	
	private List<Field> findInverseMappingFields(Class<?> clazz) {
		final List<Field> result = Generics.newArrayList();
		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			public void doWith(Field field) {
				if (isInverseMapping(field)) {
					ReflectionUtils.makeAccessible(field);
					result.add(field);
				}
			}
		});
		return result;
	}

	private boolean isInverseMapping(Field field) {
		Class<?> clazz = field.getType();
		if (clazz.isAnnotationPresent(TagCacheItems.class)) {
			ClassMetadata meta = sessionFactory.getClassMetadata(clazz);
			if (meta != null) {
				return hasInverseCollection(meta, field.getDeclaringClass());
			}
		}
		return false;
	}
	
	private boolean hasInverseCollection(ClassMetadata meta, Class<?> elementType) {
		SessionFactoryImplementor impl = (SessionFactoryImplementor) sessionFactory;
		for (Type type : meta.getPropertyTypes()) {
			if (type.isCollectionType()) {
				String role = ((CollectionType) type).getRole();
				CollectionPersister persister = impl.getCollectionPersister(role);
				return persister.isInverse() && persister.getElementType()
						.getReturnedClass().equals(elementType);
			}
		}
		return false;
	}
	
}
