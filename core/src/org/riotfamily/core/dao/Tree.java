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
package org.riotfamily.core.dao;

public interface Tree extends RiotDao {

	/**
	 * Returns the parent node of the given object. The method must return
	 * <code>null</code> for the root node(s).
	 */
	public Object getParentNode(Object node);

	/**
	 * Returns whether the given node has any children, i.e. can be expanded.
	 * Note that the <code>parent</code> argument is <em>not</em> the parent 
	 * of the given item, but the parent of the whole tree. Hence the argument
	 * will be <code>null</code> unless the tree is nested within another list.
	 */
	public boolean hasChildren(Object node, Object parent, ListParams params);
	
}
