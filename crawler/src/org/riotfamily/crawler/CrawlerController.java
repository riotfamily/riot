package org.riotfamily.crawler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class CrawlerController implements Controller {

	private Crawler crawler;
	
	public CrawlerController(Crawler crawler) {
		this.crawler = crawler;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		PrintWriter out = response.getWriter();
		if (crawler.isRunning()) {
			out.print(crawler.getProgress()  + "% completed.");
		}
		else {
			if ("start".equals(request.getParameter("action"))) {
				new Thread(crawler).start();
				out.print("Crawler started.");
			}
			else {
				out.print("<a href=\"?action=start\">Start Crawling</a>");
			}
		}
		return null;
	}
}
