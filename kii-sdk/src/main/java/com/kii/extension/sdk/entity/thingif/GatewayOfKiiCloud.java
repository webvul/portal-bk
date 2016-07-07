package com.kii.extension.sdk.entity.thingif;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by USER on 3/24/16.
 */
public class GatewayOfKiiCloud {


    private String thingID;

    private String vendorThingID;

    private String layoutPosition;

    private Boolean disabled;

    private Date created;

    private String kiiAppID;

    private String fullKiiThingID;

    private Long globalThingID;


    //
    @JsonProperty("layoutPosition")
    public String getLayoutPosition() {
        return layoutPosition;
    }
    @JsonProperty("_layoutPosition")
    public void setLayoutPosition(String layoutPosition) {
        this.layoutPosition = layoutPosition;
    }
    @JsonProperty("disabled")
    public Boolean getDisabled() {
        return disabled;
    }
    @JsonProperty("_disabled")
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
    @JsonProperty("thingID")
    public String getThingID() {
        return thingID;
    }
    @JsonProperty("_thingID")
    public void setThingID(String thingID) {
        this.thingID = thingID;
    }
    @JsonProperty("vendorThingID")
    public String getVendorThingID() {
        return vendorThingID;
    }
    @JsonProperty("_vendorThingID")
    public void setVendorThingID(String vendorThingID) {
        this.vendorThingID = vendorThingID;
    }

    @JsonProperty("_created")
    public void setCreated(Date created) {
        this.created = created;
    }
    @JsonProperty("created")
    public Date getCreated() {
        return created;
    }


    //

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

    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper m = new ObjectMapper();

        GatewayOfKiiCloud g = new GatewayOfKiiCloud();
        g.setGlobalThingID(10012L);

        System.out.println(m.writeValueAsString(g));

    }
}
