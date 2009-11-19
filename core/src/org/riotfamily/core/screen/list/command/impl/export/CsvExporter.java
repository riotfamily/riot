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
package org.riotfamily.core.screen.list.command.impl.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanWrapperImpl;

public class CsvExporter implements Exporter {

	private String encoding = "UTF-8";
	
	private String delimiter = ";";
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getFileExtension() {
		return "csv";
	}
	
	public void export(String objectId, Collection<?> items, Object parent, 
			List<String> properties, HttpServletResponse response) 
			throws IOException {
	
		response.setContentType("text/csv; charset=" + encoding);
		response.setCharacterEncoding(encoding);

		PrintWriter out = response.getWriter();
		for (Object item : items) {
			BeanWrapperImpl wrapper = new BeanWrapperImpl(item);
			for (String property : properties) {
				Object value = wrapper.getPropertyValue(property);
				if (value instanceof Collection<?>) {
					Iterator<?> it = ((Collection<?>) value).iterator();
					while (it.hasNext()) {
						out.print(it.next());
						out.print(' ');
					}
				}
				else if (value != null) {
					out.print(value);
				}
				out.print(delimiter);
			}
			out.println();
		}
	}
}
