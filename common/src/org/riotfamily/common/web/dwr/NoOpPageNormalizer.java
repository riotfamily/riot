package org.riotfamily.common.web.dwr;

import org.directwebremoting.PageNormalizer;

public class NoOpPageNormalizer implements PageNormalizer {

	public String normalizaPage(String page) {
		return page;
	}

}
