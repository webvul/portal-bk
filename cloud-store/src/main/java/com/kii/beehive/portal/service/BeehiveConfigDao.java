package com.kii.beehive.portal.service;


import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.BeehiveConfig;
import com.kii.beehive.portal.store.entity.es.EsDataSourceCfgEntry;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class BeehiveConfigDao extends AbstractDataAccess<BeehiveConfig> {



	@Override
	protected Class<BeehiveConfig> getTypeCls() {
		return BeehiveConfig.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveConfig");
	}


	public EsDataSourceCfgEntry getEsConfig(){

		return (EsDataSourceCfgEntry)super.getObjectByID(EsDataSourceCfgEntry.ES_DS_CONFIG);
	}

	public void saveConfigEntry(BeehiveConfig  cfgEntry){

		super.addEntity(cfgEntry,cfgEntry.getConfigName());
	}


}
