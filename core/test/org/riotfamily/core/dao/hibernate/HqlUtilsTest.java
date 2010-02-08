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
package org.riotfamily.core.dao.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class HqlUtilsTest {

	@Test
	public void joinsForSearchWithDot() {
		StringBuilder hql = new StringBuilder();
		HqlUtils.appendJoinsForSearch(hql, "this", "foo.bar");
		assertEquals(" left join this.foo as foo", hql.toString());
	}
	
	@Test
	public void joinsForSearchWith2Dots() {
		StringBuilder hql = new StringBuilder();
		HqlUtils.appendJoinsForSearch(hql, "this", "foo.bar.baz");
		assertEquals(" left join this.foo.bar as foo_bar", hql.toString());
	}
	
	@Test
	public void joinsForSearchWithoutDot() {
		StringBuilder hql = new StringBuilder();
		HqlUtils.appendJoinsForSearch(hql, "this", "foo");
		assertEquals("", hql.toString());
	}
	
	@Test
	public void searchWhereClauseWithDot() {
		assertEquals(
				"((this.id in (select distinct s.id from Cat s left join s.foo as foo where foo is not null and lower(str(foo.bar)) like :search)))", 
				HqlUtils.getSearchWhereClause("Cat", "this", "search", "foo.bar"));
	}
	
	@Test
	public void searchWhereClauseWithoutDot() {
		assertEquals(
				"((lower(str(this.foo)) like :search))", 
				HqlUtils.getSearchWhereClause("Cat", "this", "search", "foo"));
	}
		
	@Test
	public void testExample() {
		Cat example = new Cat("grey", new Cat("black"));
		assertEquals(
				"this.color = :color and 1 = 1 and :kitten_0 in elements(this.kitten)",
				HqlUtils.getExampleWhereClause(Cat.class, example, "this", "color", "kitten"));
	}
		
	public class Cat {
		
		private String color;
		
		private List<Cat> kitten;
		
		public Cat(String color, Cat... kitten) {
			this.color = color;
			if (kitten != null) {
				this.kitten = Arrays.asList(kitten);
			}
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public List<Cat> getKitten() {
			return kitten;
		}

		public void setKitten(List<Cat> kitten) {
			this.kitten = kitten;
		}

	}
}
