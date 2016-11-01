package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.GatewayOfKiiCloud;
import com.kii.extension.sdk.entity.thingif.LayoutPosition;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.query.ThingQueryParam;

@Component
public class GatewayService {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	private ApiAccessBuilder getBuilder(){
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info).bindToken(bindToolResolver.getToken());
	}

	/**
	 * get all endnodes of gateway
	 *
	 * @param thingID
	 * @return example
	 * 	[ {"thingID": "121323","vendorThingID":"e4746a0b"},
	 *	{"thingID": "134434","vendorThingID":"f4746a0b"} ]
	 */
	public List<EndNodeOfGateway> getAllEndNodesOfGateway(String thingID) {
		List<EndNodeOfGateway>  result=new ArrayList<>();

		QueryParam param = ConditionBuilder.newCondition().getFinalQueryParam();

		do {

			List<EndNodeOfGateway> list= this.getEndNodesOfGateway(thingID, param);
			result.addAll(list);

		}while(param.getPaginationKey()!=null);

		return result;
	}

	/**
	 * get endnodes of gateway by page
	 *
	 * @param thingID
	 * @param query
	 * @return example
	 * 	[ {"thingID": "121323","vendorThingID":"e4746a0b"},
	 *	{"thingID": "134434","vendorThingID":"f4746a0b"} ]
	 */
	public  List<EndNodeOfGateway> getEndNodesOfGateway(String thingID, QueryParam query) {

		int bestEffortLimit = query.getBestEffortLimit();
		String paginationKey = query.getPaginationKey();

		HttpUriRequest request=	getBuilder().getEndNodesOfGateway(thingID, bestEffortLimit, paginationKey).generRequest(mapper);

		String result=client.executeRequest(request);

		try{
			JsonNode node=mapper.readValue(result,JsonNode.class);

			JsonNode pageKey=node.get("nextPaginationKey");
			if (pageKey != null) {
				query.setPaginationKey(pageKey.asText());
			}else{
				query.setPaginationKey(null);
			}

			JsonParser jsonParser = node.get("results").traverse();
			CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, EndNodeOfGateway.class);
			List<EndNodeOfGateway> list=mapper.readValue(jsonParser, collectionType);

			return list;

		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}


	}





	/**
	 * query all gateway things
	 * @return
	 */
	public List<GatewayOfKiiCloud> getAllGateway() {
		return queryThingByLayoutPosition(LayoutPosition.GATEWAY.name());
	}



	public List<GatewayOfKiiCloud> queryThingByLayoutPosition(String layoutPosition) {
		List<GatewayOfKiiCloud>  result=new ArrayList<>();
		QueryParam param = ConditionBuilder.newCondition().equal("_layoutPosition", layoutPosition).getFinalQueryParam();
		ThingQueryParam thingQueryParam = new ThingQueryParam(param.getBestEffortLimit(), param.getPaginationKey(), param.getBucketQuery());
		do {
			List<GatewayOfKiiCloud> list= this.queryThing(thingQueryParam);
			result.addAll(list);
		}while(param.getPaginationKey()!=null);

		return result;
	}

	private List<GatewayOfKiiCloud> queryThing(ThingQueryParam query) {


		HttpUriRequest request=	getBuilder().getThings(query).generRequest(mapper);

		String result=client.executeRequest(request);

		try{
			JsonNode node=mapper.readValue(result,JsonNode.class);

			JsonNode pageKey=node.get("nextPaginationKey");
			if (pageKey != null) {
				query.setPaginationKey(pageKey.asText());
			}else{
				query.setPaginationKey(null);
			}

			JsonParser jsonParser = node.get("results").traverse();
			CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, GatewayOfKiiCloud.class);
			List<GatewayOfKiiCloud> list=mapper.readValue(jsonParser, collectionType);

			return list;

		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}


	}

}
