package org.riotfamily.pages.page;

/**
 * Interface that allows the programatic manipulation of the PageMap.
 * The processor is invoked whenever the pageMap is modified. Implementors
 * can use this hook to add system pages.
 */
public interface PageMapPostProcessor {

	public void process(PageMap pageMap);

}
