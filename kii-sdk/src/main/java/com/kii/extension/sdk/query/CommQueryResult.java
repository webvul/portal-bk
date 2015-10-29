//package com.kii.extension.sdk.query;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.kiicloud.platform.extension.rest.entity.KiiCommEntity;
//
//
//public class CommQueryResult {
//
//	private String nextPaginationKey;
//
//	private List<KiiCommEntity>  results=new ArrayList<KiiCommEntity>();
//
//	public String getNextPaginationKey() {
//		return nextPaginationKey;
//	}
//
//	public void setNextPaginationKey(String nextPaginationKey) {
//		this.nextPaginationKey = nextPaginationKey;
//	}
//
//	public List<KiiCommEntity> getResults() {
//		return results;
//	}
//
//	public void setResults(List<KiiCommEntity> mapList){
//		this.results=mapList;
//	}
//
//	public boolean hasNext(){
//		return StringUtils.isNotBlank(nextPaginationKey);
//	}
//}
