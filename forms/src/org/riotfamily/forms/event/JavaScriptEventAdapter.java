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
package org.riotfamily.forms.event;





/**
 * Interface to be implemented by elements that want to be notified of 
 * client-side JavaScript events.
 */
public interface JavaScriptEventAdapter {

	public String getId();
	
	public String getEventTriggerId();
	
	/**
	 * Returns a bitmask describing which client-side events should be 
	 * propagated to the server.
	 */
	public int getEventTypes();
	
	public void handleJavaScriptEvent(JavaScriptEvent event);
}
