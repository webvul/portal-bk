package com.kii.beehive.business.entity;

import java.util.ArrayList;
import java.util.List;

public class TagExpress {

	public static enum OperateType{
		And,Or;
	}

	private OperateType operate=OperateType.Or;

	private List<String> tagList=new ArrayList<String>();

	public OperateType getOperate() {
		return operate;
	}

	public void setOperate(OperateType operate) {
		this.operate = operate;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
}
