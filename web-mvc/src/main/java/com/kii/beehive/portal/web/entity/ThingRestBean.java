package com.kii.beehive.portal.web.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class ThingRestBean  {


	//0807W-F02-03-118
	private final static Pattern validVendorThingIDPattern = Pattern.compile("^\\d{4}\\w-[A-Z][\\d]{2}-\\d{2}-\\d{3}$");

	private final static Pattern locationPattern = Pattern.compile("^\\d{4}\\w-[A-Z][\\d]{2}$");


//	private Logger log = LoggerFactory.getLogger(ThingRestBean.class);

	private String location;

	private String gatewayVendorThingID;

	private Set<String> tagNames = new HashSet<>();

	private GlobalThingInfo  thingInfo= new GlobalThingInfo();

	public ThingRestBean(){

	}

	public ThingRestBean(GlobalThingInfo thing){
		BeanUtils.copyProperties(thing,thingInfo);
	}

	@JsonUnwrapped
	public GlobalThingInfo getThingInfo() {
		return thingInfo;
	}

	public void setThingInfo(GlobalThingInfo thingInfo) {
		this.thingInfo = thingInfo;
	}

	public void setGlobalThingID(Long globalThingID) {
		this.thingInfo.setId(globalThingID);
	}

	@JsonProperty("globalThingID")
	public Long getGlobalThingID() {
		return thingInfo.getId();
	}

	@JsonProperty("location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@JsonProperty("tags")
	public Set<String> getInputTags() {
		return tagNames;
	}


	public String getGatewayVendorThingID() {
		return gatewayVendorThingID;
	}

	public void setGatewayVendorThingID(String gatewayVendorThingID) {
		this.gatewayVendorThingID = gatewayVendorThingID;
	}

	public void setInputTags(Set<String> tags) {
		this.tagNames = tags;
	}

	@JsonIgnore
	public String getFullLocation(){

		String loc=location;

		if(StringUtils.isBlank(loc)){

			String vendorThingID=thingInfo.getVendorThingID();
			if(StringUtils.isNoneBlank(vendorThingID)){
				//0807W-F02-03-118
				loc= StringUtils.substring(vendorThingID,0,9);
			}
		}

		return loc;
	}

	public void verifyInput() {

		this.verifyVendorThingID();

		if (Strings.isBlank(this.thingInfo.getKiiAppID())) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","kiiAppID");
		}

		if(StringUtils.isNotBlank(location)){
			if(!locationPattern.matcher(location).matches()){
				throw new PortalException(ErrorCode.INVALID_INPUT,"field","location","data",location);
			}
		}

	}




	public void verifyInputForEndnode() {

		verifyInput();



		if (Strings.isBlank(this.thingInfo.getKiiAppID())) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","kiiAppID");
		}


		if (Strings.isBlank(thingInfo.getSchemaName())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "schemaName");
		}
		if (Strings.isBlank(thingInfo.getSchemaVersion())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "schemaVersion");

		}
		if (Strings.isBlank(getGatewayVendorThingID())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "gatewayVendorThingID");
		}
	}

	private void verifyVendorThingID() {

		String vendorThingID = this.thingInfo.getVendorThingID();

		if (Strings.isBlank(vendorThingID)) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","vendorThingID");
		}

		Matcher matcher = validVendorThingIDPattern.matcher(vendorThingID);

		if (!matcher.matches()) {
			throw new PortalException(ErrorCode.INVALID_INPUT,"field","vendorThingID","data",vendorThingID);
		}

	}

}
