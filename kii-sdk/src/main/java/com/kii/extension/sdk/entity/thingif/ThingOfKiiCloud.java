package com.kii.extension.sdk.entity.thingif;

import java.util.Date;

/**
 * Created by USER on 3/24/16.
 */
public class ThingOfKiiCloud {


    private String thingID;

    private String vendorThingID;

    private String layoutPosition;

    private Boolean disabled;

    private Date created;

    private String kiiAppID;

    private String fullKiiThingID;

    private Long globalThingID;


    public String getThingID() {
        return thingID;
    }

    public void setThingID(String thingID) {
        this.thingID = thingID;
    }

    public String getVendorThingID() {
        return vendorThingID;
    }

    public void setVendorThingID(String vendorThingID) {
        this.vendorThingID = vendorThingID;
    }

    public String getLayoutPosition() {
        return layoutPosition;
    }

    public void setLayoutPosition(String layoutPosition) {
        this.layoutPosition = layoutPosition;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getKiiAppID() {
        return kiiAppID;
    }

    public void setKiiAppID(String kiiAppID) {
        this.kiiAppID = kiiAppID;
    }

    public String getFullKiiThingID() {
        return fullKiiThingID;
    }

    public void setFullKiiThingID(String fullKiiThingID) {
        this.fullKiiThingID = fullKiiThingID;
    }

    public Long getGlobalThingID() {
        return globalThingID;
    }

    public void setGlobalThingID(Long globalThingID) {
        this.globalThingID = globalThingID;
    }


	public void fillKiiInfo(){


	}
}
