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
package org.riotfamily.components.editor;

import java.util.List;
import java.util.Map;

import org.riotfamily.components.meta.ComponentMetaData;

/**
 * Interface used by the Riot Toolbar to edit components. All methods defined
 * by this interface are exposed to the JavaScript client via DWR.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface ComponentEditor {

	public String getText(String contentId, String property);

	public void updateText(String contentId, String property, String text);
	
	public String[] updateTextChunks(String componentId, String property,
			String[] chunks);

	public List<ComponentMetaData> getComponentMetaData(String[] types);
	
	public String insertComponent(String listId, int position, String type);

	public String setType(String componentId, String type);
	
	public String renderComponent(String componentId);

	public void moveComponent(String componentId, String prevComponentId);

	public void deleteComponent(String componentId);
	
	public ToolbarState getState(Long[] containerIds);
	
	public void publish(Long[] containerIds);

	public void discard(Long[] containerIds);

	public void logout();

	public Map<String, Map<String, Object>> getTinyMCEProfiles();

}
