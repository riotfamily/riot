package org.riotfamily.common.hibernate;

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.metadata.ClassMetadata;

public final class ActiveRecordUtils {
	
	private ActiveRecordUtils() {
	}

	/**
	 * Returns the identifier of the given ActiveRecord. The id is obtained
	 * using the Hibernate meta-data API.
	 * 
	 * @see ClassMetadata#getIdentifier(Object, EntityMode)
	 */
	public static Serializable getId(ActiveRecord record) {
		return ActiveRecord.getSessionFactory()
				.getClassMetadata(getClass(record))
				.getIdentifier(record, EntityMode.POJO);
	}
	
	/**
	 * Like {@link #getId(ActiveRecord)}, but guarantees to return a non-null 
	 * value by invoking {@link ActiveRecord#save()} on unsaved records.
	 */
	public static Serializable getIdAndSaveIfNecessary(ActiveRecord record) {
		Serializable id = getId(record);
		if (id == null) {
			record.save();
		}
		return getId(record);
	}
	
	/**
	 * Returns the true underlying class of a possibly proxied record.
	 * @see Hibernate#getClass(Object)
	 */
	public static Class<?> getClass(ActiveRecord a) {
		return Hibernate.getClass(a);
	}
	
	/**
	 * Loads an ActiveRecord. The call is delegated to 
	 * {@link ActiveRecord#load(Class, Serializable)} which has only 
	 * protected visibility to encourage developers to implement their
	 * own public single-argument load-methods.
	 */
	public static ActiveRecord load(Class<? extends ActiveRecord> type, 
			Serializable id) {
		
		return ActiveRecord.load(type, id);
	}
	
	/**
	 * Returns a String representation of the given record with the pattern 
	 * <code>&lt;className&gt;#&lt;id&gt;</code> for persistent objects, or 
	 * <code>&lt;className&gt;@&lt;identityHashCode&gt;</code> if the instance
	 * is unsaved.
	 */
	public static String toString(ActiveRecord a) {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass(a).getName());
		Serializable id = getId(a);
		if (id != null) {
			sb.append('#').append(id);
		}
		else {
			sb.append('@').append(Integer.toHexString(System.identityHashCode(a)));
		}
		return sb.toString();
	}
	
	/**
	 * Returns whether a given object is the same as the specified ActiveRecord.
	 */
	public static boolean equals(ActiveRecord a, Object obj) {
		if (a == obj) {
			return true;
		}
		if (obj instanceof ActiveRecord) {
			ActiveRecord b = (ActiveRecord) obj;
			if (getClass(a).equals(getClass(b))) {
				Serializable id = getId(a);	
				return id != null && id.equals(getId(b));
			}
		}
		return false;
	}
	
}
