package com.kii.beehive.portal.web.entity;

import org.apache.logging.log4j.util.Strings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Created by USER on 7/31/16.
 */
public class LocationGeoRestBean {

	public LocationGeoRestBean(){

	}

	public LocationGeoRestBean(ThingGeo thingGeo) {
		this.setThingGeo(thingGeo);
	}

	private ThingGeo thingGeo;

	@JsonUnwrapped
	public ThingGeo getThingGeo() {
		return thingGeo;
	}

	public void setThingGeo(ThingGeo thingGeo) {
		this.thingGeo = thingGeo;
	}

	@JsonIgnore
	public void verifyInput(){

		// in the case of update location geo, doesn't validate the input
		if(thingGeo.getId() != null) {
			return;
		}

		if(thingGeo.getLat() == null) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "lat");
		}

		if(thingGeo.getLng() == null) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "lng");
		}

		if(Strings.isBlank(thingGeo.getBuildingID())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "buildingID");
		}

		Long globalThingID = thingGeo.getGlobalThingID();
		String vendorThingID = thingGeo.getVendorThingID();
		String aliThingID = thingGeo.getAliThingID();

		if(globalThingID == null && Strings.isBlank(vendorThingID) && Strings.isBlank(aliThingID)){
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "globalThingID or vendorThingID or " +
					"aliThingID");
		}

	}

	@Override
	public String toString() {
		return "LocationGeoRestBean{" +
				"thingGeo=" + thingGeo +
				'}';
	}
}
