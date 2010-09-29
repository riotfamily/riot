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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.springframework.beans.BeanWrapperImpl;

public class CsvExportCommand extends AbstractExportCommand {

	private String encoding = "UTF-8";
	
	private String delimiter = ";";
	
	private List<String> properties;
	
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	protected String getFileExtension() {
		return "csv";
	}
	
	@Override
	protected String getMimeType() {
		return "text/csv; charset=" + encoding;	
	}
	
	@Override
	protected void export(CommandContext context, Collection<?> items, OutputStream out) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, encoding));
		for (Object item : items) {
			BeanWrapperImpl wrapper = new BeanWrapperImpl(item);
			for (String property : properties) {
				Object value = wrapper.getPropertyValue(property);
				if (value instanceof Collection<?>) {
					Iterator<?> it = ((Collection<?>) value).iterator();
					while (it.hasNext()) {
						pw.print(it.next());
						pw.print(' ');
					}
				}
				else if (value != null) {
					pw.print(value);
				}
				pw.print(delimiter);
			}
			pw.println();
		}
	}
}
