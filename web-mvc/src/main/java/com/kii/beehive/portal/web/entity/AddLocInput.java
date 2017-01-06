package com.kii.beehive.portal.web.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class AddLocInput {


	private final static Pattern validVendorThingIDPattern = Pattern.compile("^\\[A-Z,0-9]{4}\\w-[A-Z][\\d]{2}-\\w{1}-\\d{3}$");

	private final static Pattern locationPattern = Pattern.compile("^\\[A-Z,0-9]{4}\\w-[A-Z][\\d]{2}$");


	private String gatewayVendorThingID;


	private List<String> locList=new ArrayList<>();


	private String vendorThingID;


	@JsonProperty("vendorGatewayID")
	public String getGatewayVendorThingID() {
		return gatewayVendorThingID;
	}

	public void setGatewayVendorThingID(String gatewayVendorThingID) {
		this.gatewayVendorThingID = gatewayVendorThingID;
	}

	@JsonProperty("locationList")
	public List<String> getLocList() {
		return locList;
	}

	public void setLocList(List<String> locList) {
		this.locList = locList;
	}


	public String getDefaultLoc(){

		return StringUtils.substring(vendorThingID,0,9);
	}

	@JsonProperty("vendorThingID")
	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	public void verify(){



		if (Strings.isBlank(vendorThingID)) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","vendorThingID");
		}


		Matcher matcher = validVendorThingIDPattern.matcher(vendorThingID);

		if (!matcher.matches()) {
			throw new PortalException(ErrorCode.INVALID_INPUT,"field","vendorThingID","data",vendorThingID);
		}

		if (!Strings.isBlank(gatewayVendorThingID)){

			matcher = validVendorThingIDPattern.matcher(gatewayVendorThingID);

			if (!matcher.matches()) {
				throw new PortalException(ErrorCode.INVALID_INPUT, "field", "vendorGatewayID", "data", gatewayVendorThingID);
			}
		}
		if(!locList.stream().allMatch(l->locationPattern.matcher(l).matches())){

			throw new PortalException(ErrorCode.INVALID_INPUT,"field","locactionList","data",locList.toString());

		};


	}
}
