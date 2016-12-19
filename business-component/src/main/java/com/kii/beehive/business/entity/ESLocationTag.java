package com.kii.beehive.business.entity;

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
	
	private int building;
	
	private int floor;
	
	private String partition;
	
	private String area;
	
	private String site;
	
	public ESLocationTag(){
		
	}
	
	public ESLocationTag(String loc){
		
		if(loc==null){
			loc="";
		}
		
		if(loc.length()>=2) {
			building=Integer.parseInt(loc.substring(0, 2));
		}
		if(loc.length()>=4){
			floor=Integer.parseInt(loc.substring(2, 4));
		}
		if(loc.length()>=5){
			partition=loc.substring(4, 5);
		}
		if(loc.length()>=7){
			area=loc.substring(6, 7);
		}
		if(loc.length()==9){
			site=loc.substring(7, 9);
		}
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
