package com.kii.beehive.portal.store.entity.configEntry;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.beehive.portal.store.entity.PortalEntity;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "configName")
@JsonSubTypes({
		@JsonSubTypes.Type(value = EsDataSourceCfgEntry.class, name = "esDSConfig"),
		@JsonSubTypes.Type(value = SecurityKey3Party.class, name = "securityKey"),
		@JsonSubTypes.Type(value = RuleEngineToken.class, name = "ruleEngineToken"),
	
})
public class BeehiveConfig extends PortalEntity {


	public String getConfigName() {
		return getId();
	}

	public void setConfigName(String configName) {
		this.setId(configName);
	}


}
