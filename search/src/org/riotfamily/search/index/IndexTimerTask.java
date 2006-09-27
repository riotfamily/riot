package org.riotfamily.search.index;

import java.io.IOException;
import java.util.TimerTask;

/**
 * TimerTask that invokes the <code>index()</code> method of an Indexer.
 */
public class IndexTimerTask extends TimerTask {

	private Indexer indexer;
	
	public IndexTimerTask(Indexer indexer) {
		this.indexer = indexer;
	}

	public void run() {
		try {
			indexer.index();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
