package com.kii.beehive.business.entity;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

public class ESLocationTag {
	
	
	/*
	       "locationTag": {
          "properties": {
            "building": {
              "type": "integer"
            },
            "floor": {
              "type": "integer"
            },
            "partition": {
              "type": "keyword"
            },
            "area": {
              "type": "keyword"
            },
            "site": {
              "type": "keyword"
            }
	 */
	
	private int building=0;
	
	private int floor=0;
	
	private String partition;
	
	private String area;
	
	private String site;
	
	public ESLocationTag(){
		
	}
	
	public static ESLocationTag getInstance(String loc){
		
		if(loc==null||(!GlobalThingInfo.locationPattern.matcher(loc).find())){
			return null;
		}
		
		ESLocationTag inst=new ESLocationTag();
		
		if(loc.length()>=2) {
			inst.building=Integer.parseInt(loc.substring(0, 2));
		}
		if(loc.length()>=4){
			inst.floor=Integer.parseInt(loc.substring(2, 4));
		}
		if(loc.length()>=5){
			inst.partition=loc.substring(4, 5);
		}
		if(loc.length()>=7){
			inst.area=loc.substring(6, 7);
		}
		if(loc.length()==9){
			inst.site=loc.substring(7, 9);
		}
		
		return inst;
	}
	
	public int getBuilding() {
		return building;
	}
	
	public void setBuilding(int building) {
		this.building = building;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void setFloor(int floor) {
		this.floor = floor;
	}
	
	public String getPartition() {
		return partition;
	}
	
	public void setPartition(String partition) {
		this.partition = partition;
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getSite() {
		return site;
	}
	
	public void setSite(String site) {
		this.site = site;
	}
}
