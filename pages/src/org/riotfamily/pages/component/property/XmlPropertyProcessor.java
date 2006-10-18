package org.riotfamily.pages.component.property;

import org.riotfamily.common.xml.XmlUtils;
import org.springframework.util.Assert;
import org.w3c.dom.Node;

public class XmlPropertyProcessor extends AbstractSinglePropertyProcessor {

	protected String convertToString(Object object) {
		if (object == null) {
			return null;
		}
		Assert.isInstanceOf(Node.class, object);
		return XmlUtils.serialize((Node) object);
	}

	protected Object resolveString(String s) {
		if (s == null) {
			return null;
		}
		return XmlUtils.parse(s).getDocumentElement();
	}

}
