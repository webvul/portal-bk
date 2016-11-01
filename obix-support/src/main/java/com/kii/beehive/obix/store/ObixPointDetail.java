package com.kii.beehive.obix.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kii.beehive.industrytemplate.PointDetail;


public class ObixPointDetail {

	private String thingSchema;

	private String fieldName;

	private PointDataType type;

	private Number  minValue;

	private Number maxValue;

	private String description;

	private String enumRangeRef;

	private String superRef;

	private boolean writable=false;

	private boolean existCur=false;

	private boolean existHistory=false;

	private String unitRef;

	private Map<String,Boolean> tagCollect=new HashMap<>();

	private EnumRange range=new EnumRange();


	public ObixPointDetail(){


	}

	public ObixPointDetail(String thingSchemaName,String fieldName,PointDetail detail){
		setFieldName(fieldName);

		this.thingSchema=thingSchemaName;

		setDescription(detail.getDisplayNameCN());
		setType(PointDataType.getInstance(detail.getType()));


		if(detail.getEnumMap()!=null) {
			range = new EnumRange(detail.getEnumMap(),type);

			type=PointDataType.Enum;

		}

		if(type==PointDataType.Int||type==PointDataType.Float){
			setMaxValue(detail.getMaximum());
			setMinValue(detail.getMinimum());
		}
		setUnitRef(detail.getUnit());
	}

	public EnumRange getRange() {
		return range;
	}

	public void setRange(EnumRange range) {
		this.range = range;
	}

	public String getSuperRef() {
		return superRef;
	}

	public void setSuperRef(String superRef) {
		this.superRef = superRef;
	}

	public Map<String, Boolean> getTagCollect() {
		return tagCollect;
	}

	public void setTagCollect(Map<String, Boolean> tagCollect) {
		this.tagCollect = tagCollect;
	}

	public void addTag(String tag){
		tagCollect.put(tag,true);
	}

	public void addTagSet(List<String> tags){

		tags.forEach(this::addTag);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public PointDataType getType() {
		return type;
	}

	public void setType(PointDataType type) {
		this.type = type;
	}

	public Number getMinValue() {
		return minValue;
	}

	public void setMinValue(Number minValue) {
		this.minValue = minValue;
	}

	public Number getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Number maxValue) {
		this.maxValue = maxValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEnumRangeRef() {
		return enumRangeRef;
	}

	public void setEnumRangeRef(String enumRangeRef) {
		this.enumRangeRef = enumRangeRef;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
		if(writable){
			addTag("writable");
		}else{
			tagCollect.remove("writable");
		}
	}

	public String getUnitRef() {
		return unitRef;
	}

	public void setUnitRef(String unitRef) {
		this.unitRef = unitRef;
	}

	public boolean isExistCur() {
		return existCur;
	}

	public void setExistCur(boolean existCur) {
		this.existCur = existCur;
		if(existCur){
			addTag("cur");
		}else{
			tagCollect.remove("cur");
		}
	}

	public boolean isExistHistory() {
		return existHistory;
	}

	public void setExistHistory(boolean existHistory) {
		this.existHistory = existHistory;
		if(existHistory){
			addTag("hist");
		}else{
			tagCollect.remove("hist");
		}
	}

	public String getThingSchema() {
		return thingSchema;
	}

	public void setThingSchema(String thingSchema) {
		this.thingSchema = thingSchema;
	}
}
