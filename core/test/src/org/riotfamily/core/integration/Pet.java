package org.riotfamily.core.integration;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.riotfamily.common.web.cache.TagCacheItems;
import org.riotfamily.components.model.ContentEntity;
import org.riotfamily.media.model.RiotImage;

@Entity
@TagCacheItems
public class Pet extends ContentEntity {

	private Long id;
	
	private Species species;
	
	private String name;
	
	private RiotImage image;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@Cascade(CascadeType.ALL)
	public RiotImage getImage() {
		return image;
	}

	public void setImage(RiotImage image) {
		this.image = image;
	}

	public static List<Pet> loadAll() {
		return query(Pet.class, "from {}").find();
	}
	
	public static Pet load(Long id) {
		return load(Pet.class, id);
	}
	
}
