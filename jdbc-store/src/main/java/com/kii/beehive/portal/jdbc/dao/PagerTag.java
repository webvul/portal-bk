package com.kii.beehive.portal.jdbc.dao;

public class PagerTag {

	private int startRow=0;

	private int pageSize=50;

	private boolean hasNext=true;

	public boolean hasNext() {
		return hasNext;
	}

	public void setHasNext(boolean next) {
		hasNext = next;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void addStartRow(int number) {
		this.startRow+=number;
	}
}
