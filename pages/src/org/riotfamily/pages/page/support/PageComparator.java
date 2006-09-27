package org.riotfamily.pages.page.support;

import java.util.Comparator;

import org.riotfamily.pages.page.Page;

/**
 * Comparator that compares two pages by looking at their positions.
 */
public class PageComparator implements Comparator {

	public static final PageComparator INSTANCE = new PageComparator();

	public int compare(Object o1, Object o2) {
		if (o1 instanceof Page && o2 instanceof Page) {
			Page p1 = (Page) o1;
			Page p2 = (Page) o2;
			return p1.getPosition() - p2.getPosition();
		}
		else {
			throw new IllegalArgumentException("Arguments must both be Pages");
		}
	}

}
