package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.beehive.portal.store.entity.configEntry.RuleEngineToken;
import com.kii.beehive.portal.store.entity.configEntry.SecurityKey3Party;
import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;

@Component
public class SecurityService {
	
	
	private static final String RULE_ENGINE = "ruleEngine";
	private static final long SECOND_IN_MIN = 20 * 1000;
	private Logger log = LoggerFactory.getLogger(SecurityService.class);
	@Autowired
	private BeehiveConfigDao configDao;
	@Autowired
	private EngineTriggerBuilder builder;
	@Autowired
	private RuleEngineService service;
	private AtomicReference<String> sysToken = new AtomicReference<>();
	private String securityKey;
	
	private AtomicReference<String> authToken = new AtomicReference<>();
	private AtomicReference<Long> sysTimeStamp = new AtomicReference<>(System.currentTimeMillis());
	
	public String getSysToken() {
		return sysToken.get();
	}
	
	@Value("${ruleengine.service.sysToken}")
	public void setSysToken(String token) {
		sysToken.set(token);
	}
	
	public String getRuleEngineToken() {
		return authToken.get();
	}
	
	@PostConstruct
	public void init() {
		
		SecurityKey3Party key = configDao.getSecurityKey();
		if (key == null) {
			key = new SecurityKey3Party();
			key.addKey(RULE_ENGINE, RandomStringUtils.randomAlphanumeric(32));
			
			configDao.saveConfigEntry(key);
			securityKey = key.getSecurityKey(RULE_ENGINE);
			
			try {
				service.setSecurityKey(securityKey, sysToken.get());
			} catch (Exception e) {
				SysMonitorMsg notice = new SysMonitorMsg();
				notice.setFrom(SysMonitorMsg.FromType.RuleEngine);
				notice.setErrorType("SecurityKeyInitFail");
				notice.setErrMessage(e.getMessage());
				SysMonitorQueue.getInstance().addNotice(notice);
			}
		} else {
			securityKey = key.getSecurityKey(RULE_ENGINE);
		}
		
		RuleEngineToken tokenEntry = configDao.getRuleEngineToken();
		
		if (tokenEntry == null) {
			tokenEntry = new RuleEngineToken();
			try {
				String token = service.refreshAuthToken(sysToken.get());
				tokenEntry.setAuthToken(token);
				authToken.set(token);
				
				configDao.saveConfigEntry(tokenEntry);
			} catch (Exception e) {
				SysMonitorMsg notice = new SysMonitorMsg();
				notice.setFrom(SysMonitorMsg.FromType.RuleEngine);
				notice.setErrorType("GetAuthTokenFail");
				notice.setErrMessage(e.getMessage());
				SysMonitorQueue.getInstance().addNotice(notice);
			}
		} else {
			authToken.set(tokenEntry.getAuthToken());
		}
		
		
	}
	
	@Scheduled(fixedRate = SECOND_IN_MIN)
	public void updateTimeStamp() {
		sysTimeStamp.set(System.currentTimeMillis());
	}
	
	
	private String getSecurityKeyByGroupName(String groupName) {
		return securityKey;
	}
	
	public void fillRequest(RequestBuilder reqBuilder, String groupName) {
		
		
		long timeStamp = sysTimeStamp.get();
		
		reqBuilder.setHeader("x-security-timestamp", String.valueOf(timeStamp));
		
		
		String body = null;
		try {
			body = StreamUtils.copyToString(reqBuilder.getEntity().getContent(), Charsets.UTF_8);
		} catch (IOException e) {
			log.error(e.getMessage());
			return;
		}
		
		StringBuilder sb = new StringBuilder(body);
		
		sb.append("siteName=").append(builder.getGroupName()).append("&");
		sb.append("securityKey=").append(getSecurityKeyByGroupName(groupName)).append("&");
		sb.append("timeStamp=").append(sysTimeStamp.get()).append("&");
		sb.append("url=").append(reqBuilder.getUri().getPath());
		
		String sign = DigestUtils.sha1Hex(sb.toString());
		
		reqBuilder.setHeader("x-security-sign", sign);
		
	}
	
	public boolean verifySign(String sign, String body, String groupName, long timeStamp, String path) {
		
		if (Math.abs(timeStamp - sysTimeStamp.get()) > SECOND_IN_MIN) {
			return false;
		}
		StringBuilder sb = new StringBuilder(body);
		
		sb.append("siteName=").append(builder.getGroupName()).append("&");
		sb.append("securityKey=").append(getSecurityKeyByGroupName(groupName)).append("&");
		sb.append("timeStamp=").append(timeStamp).append("&");
		sb.append("url=").append(path);
		
		String sign2 = DigestUtils.sha1Hex(sb.toString());
		
		return sign2.equals(sign);
		
	}
	
	
}
