package com.kii.beehive.portal.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.ruleengine.TriggerOperate;
import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.BusinessObjType;
import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;
import com.kii.beehive.portal.web.entity.HelloEntry;

@Controller
@ResponseBody
@RequestMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DemoController {
	
	private Logger log = LoggerFactory.getLogger(DemoController.class);
	
	
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private TriggerOperate operate;
	@Autowired
	private ThingTagManager thingManager;
	
	@RequestMapping(value = "/hello", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public Map<String,Object> hello() {

		HelloEntry entry = new HelloEntry();
		entry.setName("hello");
		entry.setValue("world");

		CustomData data=new CustomData();

		return data.getData();

	}
	
	@RequestMapping(value = "/echo", method = {RequestMethod.POST})
	public HelloEntry echo(@RequestBody HelloEntry entry) {

//		entry.setName("hello");
//		entry.setValue("world");

		return entry;

	}
	
	@RequestMapping(value = "/demo/mlTask/{taskID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public Map<String, Object> getDemoValue(@PathVariable("taskID") String taskID) {
		
		
		int seed = (int) (System.currentTimeMillis() % 10 - 5);
		Map<String, Object> demo = new HashMap<>();
		
		demo.put("foo", 3 * seed);
		demo.put("bar", -7 * seed);
		demo.put("taskID", taskID);
		return demo;
	}
	
	@RequestMapping(value = "/demo/trigger/statusUpload/{thingID}", method = {RequestMethod.PUT})
	public void setDemoStatusValue(@PathVariable("thingID") long thingID, @RequestBody Map<String, Object> map) {
		
		
		BusinessDataObject data = new BusinessDataObject();
		data.setData(map);
		data.setBusinessType(BusinessObjType.Thing);
		
		String vendorThingID = thingManager.getThingByID(thingID).getVendorThingID();
		
		data.setBusinessObjID(vendorThingID);
		
		operate.addBusinessData(data);
	}
	
	
	@RequestMapping(value = "/demo/monitor/add", method = {RequestMethod.PUT})
	public void addMsg(@RequestBody Map<String, String> values) {
		SysMonitorMsg msg = new SysMonitorMsg();
		
		msg.setFrom(SysMonitorMsg.FromType.DB);
		msg.setFireDate(new Date());
		msg.setErrorType("mock");
		msg.setErrMessage(values.get("msg"));
		
		SysMonitorQueue.getInstance().addNotice(msg);
	}
	
	@RequestMapping(value = "/debug/monitor", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE}, produces = {"text/event-stream"})
	public SseEmitter pushMsg(HttpServletRequest request) {
		
		SseEmitter sseEmitter = new SseEmitter();
		
		List<SysMonitorMsg> msgList = SysMonitorQueue.getInstance().getMsgHistory();
		
		for (SysMonitorMsg msg : msgList) {
			try {
				String json = mapper.writeValueAsString(msg);
				
				sseEmitter.send(json);
			} catch (IOException e) {
				log.error(e.getMessage());
				break;
			}
		}
		
		HttpSession session = request.getSession(true);
		
		SysMonitorQueue.getInstance().registerFire(session.getId(), (msg) -> {
			try {
				String json = mapper.writeValueAsString(msg);
				
				sseEmitter.send(json);
				
				return true;
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
				return true;
			} catch (IOException e) {
				log.error(e.getMessage());
				sseEmitter.completeWithError(e);
				return false;
			}
			
		});
		
		return sseEmitter;
	}
	
	@RequestMapping(value = "/debug/monitor/history", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public List<SysMonitorMsg> getSysNoticeMsgHistory() {
		
		return SysMonitorQueue.getInstance().getMsgHistory();
		
	}
	
	
	
	
}
