package org.riotfamily.components.support;

import org.directwebremoting.extend.PageNormalizer;

public class NoOpPageNormalizer implements PageNormalizer {

	public String normalizePage(String page) {
		return page;
	}

}
