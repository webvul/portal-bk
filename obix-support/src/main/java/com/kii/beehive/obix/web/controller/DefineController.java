package com.kii.beehive.obix.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.kii.beehive.obix.dao.DefineContractDao;
import com.kii.beehive.obix.web.entity.ObixContain;


@RestController
@RequestMapping(path="/def/contract",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DefineController {


	@Autowired
	private  DefineContractDao  defineDao;


	@RequestMapping(path="/{contractName}" )
	public ObixContain getDefine(@PathVariable("contractName") String name,UriComponentsBuilder builder){


		ObixContain  define=defineDao.getDefineContract(StringUtils.capitalize(name));

		UriComponentsBuilder  baseBuilder=builder.pathSegment("def","contract");

		String baseUrl=baseBuilder.toUriString();

		define.setIs(getExpendUrl(define.getIs(),baseUrl));

		define.setOf(getExpendUrl(define.getOf(),baseUrl));

		String fullPath=builder.pathSegment(name).toUriString();

		define.setHref(fullPath);

		define.getChildren().forEach((c)->{

			c.setIs(getExpendUrl(c.getIs(),baseUrl));
		});



		return define;
	}

	private String getExpendUrl(String isStr,String baseUrl){
		if(StringUtils.isBlank(isStr)){
			return "";
		}
		String[] list=StringUtils.split(isStr," ");

		for(int i=0;i<list.length;i++) {
			if (list[i].startsWith("/")) {

				list[i]=baseUrl+list[i];
			}
		}

		return StringUtils.join(list," ");
	}

}