package org.riotfamily.website.performance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * Utility class that can be used to add a timestamp to a URL.
 * 
 * @see ExpiresHeaderFilterPlugin
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ResourceStamper implements InitializingBean {

	public static final String DFAULT_TIME_PATTERN = "ddHHmm";
	
	public static final Pattern DEFAULT_RECOGNITION_PATTERN =
			Pattern.compile("(^.*/\\d{14}/.+$)|(^.+[?&][0-9]+$)");

	private String timePattern = DFAULT_TIME_PATTERN;
	
	private Pattern recognitionPattern = DEFAULT_RECOGNITION_PATTERN;
	
	private String startupTime;
	
	private SimpleDateFormat format;
	
	
	public void setTimePattern(String timePattern) {
		this.timePattern = timePattern;
	}
	
	public void setRecognitionPattern(Pattern recognitionPattern) {
		this.recognitionPattern = recognitionPattern;
	}
	
	public void afterPropertiesSet() throws Exception {
		format = new SimpleDateFormat(timePattern);
		startupTime = now();
	}
	
	private String now() {
		return format.format(new Date());
	}
	
	public String stamp(String url) {
		return stamp(url, false);
	}
	
	public String stamp(String url, boolean now) {
		if (isStamped(url)) {
			return url;
		}
		return ServletUtils.addParameter(url, now ? now() : startupTime, null);
	}
	
	protected boolean isStamped(String url) {
		return recognitionPattern.matcher(url).matches();
	}

}
