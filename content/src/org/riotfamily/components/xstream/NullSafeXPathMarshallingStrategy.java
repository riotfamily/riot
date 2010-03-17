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
package org.riotfamily.components.xstream;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathUnmarshaller;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class NullSafeXPathMarshallingStrategy extends
		ReferenceByXPathMarshallingStrategy implements MarshallingStrategy {

	public NullSafeXPathMarshallingStrategy() {
		super(RELATIVE);
	}

	@Override
	protected TreeUnmarshaller createUnmarshallingContext(Object root,
			HierarchicalStreamReader reader, ConverterLookup converterLookup,
			Mapper mapper) {
		
		return new NullSafeXPathUnmarshaller(root, reader, converterLookup, mapper);
	}
	
	private static class NullSafeXPathUnmarshaller extends ReferenceByXPathUnmarshaller {

		public NullSafeXPathUnmarshaller(Object root,
				HierarchicalStreamReader reader,
				ConverterLookup converterLookup, Mapper mapper) {
			
			super(root, reader, converterLookup, mapper);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		protected Object convert(Object parent, Class type, Converter converter) {
			try {
				return super.convert(parent, type, converter);
			}
			catch (ConversionException e) {
				String ref = e.get("reference");
				if (ref != null) {
					return null;
				}
				throw e;
			}
		}
	}
}
