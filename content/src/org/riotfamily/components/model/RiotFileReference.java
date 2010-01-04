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
		query(RiotFileReference.class,
				"delete from {} where content = ?", content)
				.executeUpdate();
	}
}
