package com.kii.beehive.obix.web.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ObixContain {



	private ObixType obix=ObixType.OBJ;

	private String href;

	private String is;

	private String name;

	private String unit;

	private Object val;

	private Number min;

	private Number max;

	private String of;

	private String display;

	private String displayName;

	private String range;

	private String in;

	private String out;

	private String status;

	private Integer precision;

	private String tz;

	private boolean writable;

	private Set<ObixContain>  children=new HashSet<>();

	@JsonIgnore
	public ObixType getObixType() {
		return obix;
	}

	public void setObixType(ObixType obix) {
		this.obix = obix;
	}

	public String getObix(){
		return obix.name().toLowerCase();
	}

	public void setObix(String obix){
		this.obix=ObixType.valueOf(obix.toUpperCase());
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getIs() {
		return is;
	}

	public void setIs(String is) {
		this.is = is;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public Number getMin() {
		return min;
	}

	public void setMin(Number min) {
		this.min = min;
	}

	public Number getMax() {
		return max;
	}

	public void setMax(Number max) {
		this.max = max;
	}

	public String getOf() {
		return of;
	}

	public void setOf(String of) {
		this.of = of;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public String getTz() {
		return tz;
	}

	public void setTz(String tz) {
		this.tz = tz;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public Set<ObixContain> getChildren() {
			return children;
	}

	public void setChildren(Set<ObixContain> children) {
		this.children = children;
	}

	public void addChild(ObixContain child){
		this.children.add(child);
	}


}
