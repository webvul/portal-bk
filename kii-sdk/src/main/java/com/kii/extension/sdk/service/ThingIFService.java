package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.ActionResult;
import com.kii.extension.sdk.entity.thingif.CommandDetail;
import com.kii.extension.sdk.entity.thingif.CommandQuery;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class ThingIFService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	@Autowired
	private TokenBindToolResolver tool;


	private ApiAccessBuilder getBuilder(){
		AppInfo info= bindToolResolver.getAppInfo();

		return new ApiAccessBuilder(info).bindToken(tool.getToken());
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

}
