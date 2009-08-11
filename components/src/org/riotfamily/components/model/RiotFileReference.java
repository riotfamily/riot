package org.riotfamily.components.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.riotfamily.common.hibernate.ActiveRecordFieldSupport;
import org.riotfamily.media.model.RiotFile;

@Entity
@Table(name="riot_file_references")
@SuppressWarnings("unused")
public class RiotFileReference extends ActiveRecordFieldSupport {

	@ManyToOne
	private Content content;
	
	@ManyToOne
	private RiotFile file;
	
	public RiotFileReference() {
	}
	
	public RiotFileReference(Content content, RiotFile file) {
		this.content = content;
		this.file = file;
	}

	public static void deleteByContent(Content content) {
		createQuery("delete from " + RiotFileReference.class.getName()
				+ " where content = ?", content).executeUpdate();
	}
}
