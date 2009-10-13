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
package org.riotfamily.media.processing;

import java.io.File;
import java.io.IOException;

/**
 * Interface to render thumbnail images.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface Thumbnailer {

	/**
	 * Renders a thumbnail image of the source file to the specified 
	 * destination.
	 */
	public void renderThumbnail(File source, File dest, int width, int height,
			boolean fixedSize, String backgroundColor) throws IOException;
	
}
