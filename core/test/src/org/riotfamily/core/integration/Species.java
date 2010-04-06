package org.riotfamily.core.integration;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.web.cache.TagCacheItems;

@Entity
@TagCacheItems
public class Species extends ActiveRecordBeanSupport {

	private String name;
	
	private Set<Pet> pets;

	@OneToMany(mappedBy="species")
	public Set<Pet> getPets() {
		return pets;
	}

	public void setPets(Set<Pet> pets) {
		this.pets = pets;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
