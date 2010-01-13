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
package org.riotfamily.common.collection;

import static junit.framework.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

public class SerializationSafeHashMapTests {

	private SerializationSafeHashMap<Object, Object> map;

	private static class NonSerializableValue {
		
		String value;
		
		private NonSerializableValue(String value) {
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			return value.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NonSerializableValue) {
				NonSerializableValue other = (NonSerializableValue) obj;
				return value.equals(other.value);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	@Before
	public void createMap() {
		map = new SerializationSafeHashMap<Object, Object>();
		map.put("key1", "value1");
		map.put(new NonSerializableValue("key2"), "value2");
		map.put("key3", new NonSerializableValue("value3"));	
	}
	
	@Test	
	@SuppressWarnings("unchecked")
	public void serialization() throws Exception {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(target);
		out.writeObject(map);
		out.close();
		
		byte[] bytes = target.toByteArray();
		assertTrue("Serialized data must not be empty", bytes.length > 0);
		assertTrue("Original Map must not be modified", map.size() == 3);
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
		Object obj = in.readObject();
		in.close();
		assertTrue("Deserialized object must be of same type", obj instanceof SerializationSafeHashMap<?, ?>);
		SerializationSafeHashMap<Object, Object> map2 = (SerializationSafeHashMap) obj;
		
		assertTrue("Deserialized map must contain 'key1'", map2.containsKey("key1"));
		assertEquals("Size of the seserialized map must be 1", map2.size(), 1);	
	}
	
}
