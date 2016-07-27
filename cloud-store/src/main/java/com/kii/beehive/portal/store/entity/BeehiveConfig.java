package com.kii.beehive.portal.store.entity;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.beehive.portal.store.entity.es.EsDataSourceCfgEntry;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "configName")
@JsonSubTypes({
		@JsonSubTypes.Type(value = EsDataSourceCfgEntry.class, name = "esDSConfig")
})
public  class BeehiveConfig  extends PortalEntity{


	public String getConfigName() {
		return getId();
	}

	public void setConfigName(String configName) {
		this.setId(configName);
	}


}
