package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.CommandDetail;
import com.kii.extension.sdk.entity.thingif.CommandQuery;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.ActionResult;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;

@Component
public class ThingIFService {

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

	public String sendCommand(String thingID,ThingCommand command){
		/*
		http://api.kii.com/thing-if/apps/aa407bbe/targets/THING:th.75026fa00022-0819-5e11-b899-01595ce7/commands
		 */

		HttpUriRequest request=	getBuilder().sendCommand(thingID,command).generRequest(mapper);

		Map<String,Object> result=client.executeRequestWithCls(request,Map.class);

		return (String)result.get("commandID");
	}

	public CommandDetail readCommand(String thingID, String commandID){


		HttpUriRequest request=	getBuilder().getCommand(thingID,commandID).generRequest(mapper);

		CommandDetail result=client.executeRequestWithCls(request,CommandDetail.class);

		return result;
	}

	public List<CommandDetail> queryCommand(String thingID, CommandQuery query){

		HttpUriRequest request=	getBuilder().queryCommands(thingID,query.getBestLimit(),query.getNextPaginationKey()).generRequest(mapper);

		String result=client.executeRequest(request);

		try{
			JsonNode node=mapper.readValue(result,JsonNode.class);

			JsonNode pageKey=node.get("nextPaginationKey");
			if (pageKey != null) {
				query.setNextPaginationKey(pageKey.asText());
			}else{
				query.setNextPaginationKey(null);
			}

			List<CommandDetail> list=mapper.readValue(node.get("commands").traverse(),mapper.getTypeFactory().constructCollectionType(List.class, CommandDetail.class));

			return list;

		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}

	}

	public ThingStatus getStatus(String thingID){


		HttpUriRequest request=	getBuilder().getThingStatus(thingID).generRequest(mapper);

		ThingStatus result=client.executeRequestWithCls(request,ThingStatus.class);

		return result;
	}

	public OnBoardingResult onBoarding(OnBoardingParam param){

		HttpUriRequest request=	getBuilder().thingOnboarding(param).generRequest(mapper);

		OnBoardingResult result=client.executeRequestWithCls(request,OnBoardingResult.class);

		return result;

	}

	public void submitActionResult(String thingID,String commandID,List<Map<String,ActionResult>> resultList){

		HttpUriRequest request=	getBuilder().submitCommand(thingID,commandID,resultList).generRequest(mapper);

		client.executeRequest(request);
	}

	public void putStatus(String thingID,ThingStatus status){

		HttpUriRequest request=	getBuilder().setThingStatus(thingID,status).generRequest(mapper);

		client.executeRequest(request);


	}

	/**
	 * remove thing by Kii thing id
	 * @param thingID Kii thing id
     */
	public void removeThing(String thingID) {

		HttpUriRequest request=getBuilder().deleteThing(thingID).generRequest(mapper);

		client.executeRequest(request);

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
	private List<EndNodeOfGateway> getEndNodesOfGateway(String thingID, QueryParam query) {

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

}
