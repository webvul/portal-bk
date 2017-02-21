package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.extension.sdk.context.AdminTokenBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.ActionResult;
import com.kii.extension.sdk.entity.thingif.InstallationID;
import com.kii.extension.sdk.entity.thingif.InstallationInfo;
import com.kii.extension.sdk.entity.thingif.MqttEndPoint;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingOfKiiCloud;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.exception.MQTTNotReadyException;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;
import com.kii.extension.sdk.query.QueryParam;

@Component
public class ThingIFService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private KiiCloudClient client;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	@Autowired
	private AdminTokenBindTool  adminTokenTool;

	
	private Logger log= LoggerFactory.getLogger(ThingIFService.class);

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

	public ThingCommand readCommand(String thingID, String commandID){


		HttpUriRequest request=	getBuilder().getCommand(thingID,commandID).generRequest(mapper);

		ThingCommand result=client.executeRequestWithCls(request,ThingCommand.class);

		return result;
	}

//	public List<ThingCommand> queryCommand(String thingID, CommandQuery query){
//
//		HttpUriRequest request=	getBuilder().queryCommands(thingID,query.getBestLimit(),query.getNextPaginationKey()).generRequest(mapper);
//
//		String result=client.executeRequest(request);
//
//		try{
//			JsonNode node=mapper.readValue(result,JsonNode.class);
//
//			JsonNode pageKey=node.get("nextPaginationKey");
//			if (pageKey != null) {
//				query.setNextPaginationKey(pageKey.asText());
//			}else{
//				query.setNextPaginationKey(null);
//			}
//
//			List<ThingCommand> list=mapper.readValue(node.get("commands").traverse(),mapper.getTypeFactory().constructCollectionType(List.class, ThingCommand.class));
//
//			return list;
//
//		}catch(IOException e){
//			throw new IllegalArgumentException(e);
//		}
//
//	}

	public List<ThingCommand> queryCommandFull(String thingID, QueryParam query){
		List<ThingCommand>  result=new ArrayList<ThingCommand>();
		do {
			List<ThingCommand> list=queryCommand(thingID, query);
			result.addAll(list);
		}while(query.getPaginationKey()!=null);
		return result;
	}

	public void deleteCommand(String thingID, String commandId) {

		HttpUriRequest request = getBuilder().deleteCommand(thingID, commandId).generRequest(mapper);

		String result = client.executeRequest(request);

	}
	public List<ThingCommand> queryCommand(String thingID, QueryParam query){

		HttpUriRequest request=	getBuilder().queryCommands(thingID, query ).generRequest(mapper);

		String result=client.executeRequest(request);

		try{
			JsonNode node=mapper.readValue(result,JsonNode.class);

			JsonNode pageKey=node.get("nextPaginationKey");
			if (pageKey != null) {
				query.setPaginationKey(pageKey.asText());
			}else{
				query.setPaginationKey(null);
			}

			List<ThingCommand> list=mapper.readValue(node.get("results").traverse(),mapper.getTypeFactory().constructCollectionType(List.class, ThingCommand.class));

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


	public ThingOfKiiCloud getThingGateway(String thingID){


		HttpUriRequest request=	getBuilder().getThingGateway(thingID).generRequest(mapper);

		ThingOfKiiCloud result=client.executeRequestWithCls(request,ThingOfKiiCloud.class);

		return result;
	}



	public OnBoardingResult onBoarding(OnBoardingParam param){

		HttpUriRequest request=	getBuilder().thingOnboardingByOwner(param).generRequest(mapper);

		OnBoardingResult result=client.executeRequestWithCls(request,OnBoardingResult.class);

		return result;

	}

	public OnBoardingResult thingOnBoarding(OnBoardingParam param){

		AppInfo info= bindToolResolver.getAppInfo();

		ApiAccessBuilder builder= new ApiAccessBuilder(info);

		HttpUriRequest request=	builder.thingOnboarding(param).generRequest(mapper);

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

	public InstallationID registerInstallactionByAdmin(String thingID){

		AppInfo info= bindToolResolver.getAppInfo();


		HttpUriRequest  request=new ApiAccessBuilder(info).bindToken(adminTokenTool.getToken())
				.getThingInstallationByAdmin(thingID)
				.generRequest(mapper);


		return client.executeRequestWithCls(request,InstallationID.class);

	}

	public InstallationID registerInstallaction(){


		HttpUriRequest  request=this.getBuilder()
				.getThingInstallation()
				.generRequest(mapper);

		return client.executeRequestWithCls(request,InstallationID.class);

	}


	public InstallationInfo getInstallationInfoByID(String installationID){

		HttpUriRequest  request=this.getBuilder()
				.getInstallationByID(installationID)
				.generRequest(mapper);

		return client.executeRequestWithCls(request,InstallationInfo.class);

	}

	public List<InstallationInfo> getInstallationInfosByThingID(String thingID){

		HttpUriRequest  request=this.getBuilder()
				.getInstallationsByThingID(thingID)
				.generRequest(mapper);

		InstallationInfos infos= client.executeRequestWithCls(request,InstallationInfos.class);

		return Arrays.asList(infos.getInstallations());
	}


	public MqttEndPoint getMQTTByInstallationID(String installationID){

		HttpUriRequest request=this.getBuilder().getMQTTEndPointByInstallationID(installationID).generRequest(mapper);

		int i=10;
		while(i>0) {
			try {

				return client.executeRequestWithCls(request, MqttEndPoint.class);
			} catch (MQTTNotReadyException ex){

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
			}
			i--;
		}

		return null;

	}



	private  static class InstallationInfos{

		private InstallationInfo[] installations=new InstallationInfo[0];

		public InstallationInfo[] getInstallations() {
			return installations;
		}

		public void setInstallations(InstallationInfo[] installations) {
			this.installations = installations;
		}
	}

}
