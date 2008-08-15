package org.riotfamily.statistics.domain;

public class CacheRegionStatistics extends NamedEntity {
	
	private Long elementsInMemory;
	
	private Long elementsOnDisk;
	
	private Long hitCount;
	
	private Long missCount;
	
	private Long putCount;
	
	private Long kbInMemory;

	public CacheRegionStatistics(String name) {
		super(name);
	}
	
	public Long getElementsInMemory() {
		return elementsInMemory;
	}

	public void setElementsInMemory(Long elementsInMemory) {
		this.elementsInMemory = elementsInMemory;
	}

	public Long getElementsOnDisk() {
		return elementsOnDisk;
	}

	public void setElementsOnDisk(Long elementsOnDisk) {
		this.elementsOnDisk = elementsOnDisk;
	}

	public Long getHitCount() {
		return hitCount;
	}

	public void setHitCount(Long hitCount) {
		this.hitCount = hitCount;
	}

	public Long getMissCount() {
		return missCount;
	}

	public void setMissCount(Long missCount) {
		this.missCount = missCount;
	}

	public Long getPutCount() {
		return putCount;
	}

	public void setPutCount(Long putCount) {
		this.putCount = putCount;
	}

	public Long getKbInMemory() {
		return kbInMemory;
	}

	public void setKbInMemory(Long kbInMemory) {
		this.kbInMemory = kbInMemory;
	}

}
