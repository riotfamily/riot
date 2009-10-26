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
package org.riotfamily.linkcheck;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.springframework.util.ObjectUtils;

@Embeddable
public class BrokenLinkPK implements Serializable {
	
	private String destination;
	
	private String source;

	public BrokenLinkPK() {
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}	

	@Override
	public int hashCode() {
		int result = 1;
		if (getSource() != null) {
			result += getSource().hashCode();
		}
		if (getDestination() != null) {
			result += getDestination().hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof BrokenLinkPK) {
			BrokenLinkPK other = (BrokenLinkPK) obj;
			return ObjectUtils.nullSafeEquals(getSource(), other.getSource())
					&& ObjectUtils.nullSafeEquals(getSource(), other.getDestination());
		}
		return false;
	}

}
