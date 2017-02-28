package com.kii.beehive.portal.service;


import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.configEntry.BeehiveConfig;
import com.kii.beehive.portal.store.entity.configEntry.EsDataSourceCfgEntry;
import com.kii.beehive.portal.store.entity.configEntry.RuleEngineToken;
import com.kii.beehive.portal.store.entity.configEntry.SecurityKey3Party;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@Component
public class BeehiveConfigDao extends BaseKiicloudDao<BeehiveConfig> {



	@Override
	protected Class<BeehiveConfig> getTypeCls() {
		return BeehiveConfig.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveConfig");
	}
	
	
	public SecurityKey3Party getSecurityKey() {
		
		return getConfig(SecurityKey3Party.SECURITY_KEY);
		
	}
	

	public EsDataSourceCfgEntry getEsConfig(){
		
		return getConfig(EsDataSourceCfgEntry.ES_DS_CONFIG);
	}

	public void saveConfigEntry(BeehiveConfig  cfgEntry){

		super.addEntity(cfgEntry,cfgEntry.getConfigName());
	}
	
	
	public RuleEngineToken getRuleEngineToken() {
		return getConfig(RuleEngineToken.RULENGINE_TOKEN);
	}
	
	private <T> T getConfig(String name) {
		try {
			return (T) super.getObjectByID(name);
		} catch (ObjectNotFoundException e) {
			return null;
		}
	}
}
