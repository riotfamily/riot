package org.riotfamily.website.hyphenate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import net.davidashen.text.Hyphenator;
import net.davidashen.util.ErrorHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

public class RiotHyphenator implements InitializingBean {
	
	private static final Log log = LogFactory.getLog(RiotHyphenator.class);
	
	private File baseDir;
	
	private Map<String, Hyphenator> hyphenators = Generics.newHashMap();

	public void setBaseDir(Resource resource) throws IOException {
		this.baseDir = resource.getFile();
	}
	
	public void afterPropertiesSet() throws Exception {
		if (baseDir != null && baseDir.exists()) {
			log.debug("Searching for *.tex files in " + baseDir.getAbsolutePath());
			File[] files = baseDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (FormatUtils.getExtension(file.getName()).equals("tex")) {
					String localeCode = FormatUtils.stripExtension(file.getName());
					try {
						Hyphenator hyphenator = new Hyphenator();
						hyphenator.setErrorHandler(new CommonsLoggingErrorHandler(file.getName()));
						hyphenator.loadTable(new FileInputStream(file));				
						hyphenators.put(localeCode, hyphenator);
					} 
					catch (ErrorHandler.NotSetException e) {
						log.error("Could not load hyphenation table for locale "
								+ localeCode + e.getMessage());
					}
				}
			}			
		}
	}
	
	public String hyphenate(Locale locale, String text) {
		Hyphenator hyphenator = (Hyphenator) hyphenators.get(locale.toString());
		if (hyphenator == null) {
			log.debug("couldn't find hyphenator for locale " + locale 
					+ ", now trying to find a hyphenator for language " 
					+ locale.getLanguage());
			
			hyphenator = (Hyphenator) hyphenators.get(locale.getLanguage());
		}
		
		if (hyphenator != null) {
			String result = hyphenator.hyphenate(text, 2, 2);
			if (log.isDebugEnabled()) {
				log.debug("Hyphenator result: " + result.replaceAll("\u00AD", "-"));
			}
			return result;
		}
		log.warn("No hyphenator found for locale: " + locale);
		return text;
	}

}
