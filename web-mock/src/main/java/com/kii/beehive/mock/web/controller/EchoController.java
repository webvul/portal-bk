package com.kii.beehive.mock.web.controller;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.mock.web.data.MockResult;
import com.kii.beehive.mock.web.data.MockResultDao;

@RestController
@RequestMapping(path="/",consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class EchoController {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MockResultDao dao;

	@RequestMapping(path="/echo/{method}",method={RequestMethod.POST})
	public void echo(@RequestBody String context, @PathVariable("method") String method,HttpServletRequest request) throws IOException {



		String ctxPath=request.getContextPath();

		JsonNode jsonNode=mapper.readValue(context,JsonNode.class);

		MockResult result=new MockResult();
		result.setResult(jsonNode);
		result.setMethod(method);

		dao.addEntity(result);

		return;

	}


}