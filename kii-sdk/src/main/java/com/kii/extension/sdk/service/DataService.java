package com.kii.extension.sdk.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.CreateResponse;
import com.kii.extension.sdk.entity.UpdateResponse;
import com.kii.extension.sdk.impl.ApiAccessBuilder;
import com.kii.extension.sdk.impl.KiiCloudClient;
import com.kii.extension.sdk.query.QueryParam;

@Component
public class DataService {

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


	public <T> List<T> query(QueryParam query,Class<T>  cls,BucketInfo bucketInfo){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucketInfo).query(query).generRequest(mapper);


		String result= client.executeRequest(request);

		try {
			JsonNode node=mapper.readValue(result,JsonNode.class);

			JsonNode pageKey=node.get("nextPaginationKey");
			if (pageKey != null) {
				query.setPaginationKey(pageKey.asText());
			}else{
				query.setPaginationKey(null);
			}

			List<T> list=mapper.readValue(node.get("results").traverse(),mapper.getTypeFactory().constructCollectionType(List.class, cls));

			return list;

		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	public <T> T  getObjectByID(String id,BucketInfo bucket,Class<T> cls){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).getObjectByID(id).generRequest(mapper);

		return client.executeRequestWithCls(request,cls);

	}

	public <T> CreateResponse createObject(T obj,BucketInfo bucket){

		HttpUriRequest  request=getBuilder().bindBucketInfo(bucket).create(obj).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);

		String body= HttpUtils.getResponseBody(response);

		try {
			return mapper.readValue(body, CreateResponse.class);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}

	public void removeObject(String id,BucketInfo bucket){

		HttpUriRequest  request=getBuilder().bindBucketInfo(bucket).delete(id).generRequest(mapper);

		client.executeRequest(request);


	}


	public void removeObjectWithVersion(String id,String version,BucketInfo bucket){

		HttpUriRequest  request=getBuilder().bindBucketInfo(bucket).delete(id,version).generRequest(mapper);

		client.executeRequest(request);


	}


	public <T> UpdateResponse  fullUpdateObject(String id,T obj,BucketInfo bucket){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).updateAll(id, obj).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);

		return getUpdateResponse(response);
	}

	private UpdateResponse getUpdateResponse(HttpResponse response) {
		String body= HttpUtils.getResponseBody(response);

		String version=response.getFirstHeader("ETag").getValue();


		UpdateResponse info= null;
		try {
			info = mapper.readValue(body,UpdateResponse.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		info.setVersion(version);
		info.setIsUpdate(response.getHeaders("Location")==null);

		return info;
	}


	public <T> UpdateResponse  fullUpdateObjectWithVersion(String id,T obj,BucketInfo bucket,String version){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).updateAllWithVersion(id, obj,version).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);

		return getUpdateResponse(response);
	}

	public boolean checkObjectExist(String id,BucketInfo bucket){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).header(id).generRequest(mapper);

		client.shutdownExceptionFactory();

		HttpResponse response = client.doRequest(request);

		return response.getStatusLine().getStatusCode()==204;
	}



	public String  updateObjectWithVersion(String id,Map<String,Object> obj,BucketInfo bucket,String version){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).updateWithVersion(id, obj,version).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);


		String newVer=response.getFirstHeader("ETag").getValue();


		return newVer;
	}

	public String  updateObject(String id,Map<String,Object> obj,BucketInfo bucket){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).update(id, obj).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);

		String newVer=response.getFirstHeader("ETag").getValue();


		return newVer;
	}

	public String  updateObjectWithEntity(String id,Object obj,BucketInfo bucket){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).update(id, obj).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);

		String newVer=response.getFirstHeader("ETag").getValue();


		return newVer;
	}

	public String  updateObjectWithVersionWithEntity(String id,Object obj,BucketInfo bucket,String version){

		HttpUriRequest request=getBuilder().bindBucketInfo(bucket).updateWithVersion(id, obj,version).generRequest(mapper);

		HttpResponse  response= client.doRequest(request);


		String newVer=response.getFirstHeader("ETag").getValue();


		return newVer;
	}

}
