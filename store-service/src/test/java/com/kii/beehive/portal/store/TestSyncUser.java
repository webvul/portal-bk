//package com.kii.beehive.portal.store;
//
//
//import static org.mockito.Matchers.any;
//
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.concurrent.FutureCallback;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.rule.PowerMockRule;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.beehive.portal.helper.NotifySenderTool;
//import com.kii.beehive.portal.helper.PortalTokenService;
//import com.kii.beehive.portal.helper.SyncMsgService;
//import com.kii.beehive.portal.service.UserSyncMsgDao;
//import com.kii.beehive.portal.store.entity.DeviceSupplier;
//import com.kii.beehive.portal.store.entity.usersync.UserSyncMsg;
//import com.kii.extension.sdk.impl.KiiCloudClient;
//import com.kii.extension.sdk.service.DataService;
//
//@PrepareForTest({KiiCloudClient.class,UserSyncMsgDao.class,PortalTokenService.class})
//public class TestSyncUser extends TestInit{
//
//	@Rule
//	public PowerMockRule rule = new PowerMockRule();
//
//
//	@Autowired
//	private NotifySenderTool tool;
//
//	@Autowired
//	private UserSyncMsgDao msgDao;
//
//	@Autowired
//	private KiiCloudClient client;
//
//	@Autowired
//	private PortalTokenService tokenService;
//
//	private String supplierSource="a";
//
//	@Before
//	public void init(){
//
//		PowerMockito.doAnswer(new Answer<DeviceSupplier>(){
//
//
//			@Override
//			public DeviceSupplier answer(InvocationOnMock invocation) throws Throwable {
//
//				DeviceSupplier supplier=new DeviceSupplier();
//				supplier.setName(supplierSource);
//
//				return supplier;
//			}
//		}).when(tokenService).getSupplierInfo();
//
//
//		PowerMockito.doAnswer(new Answer<Void>() {
//
//			@Override
//			public Void answer(InvocationOnMock invocation) throws Throwable {
//
//
//
//				return null;
//			}
//		}).when(client).syncExecuteRequest(any(HttpUriRequest.class),any(FutureCallback.class));
//
//
//		UserSyncMsgDao  syncMsgDao=PowerMockito.mock(UserSyncMsgDao.class);
//
//
//		PowerMockito.doAnswer(new Answer<Void>() {
//
//			@Override
//			public Void answer(InvocationOnMock invocation) throws Throwable {
//
//
//
//				return null;
//			}
//		}).when(syncMsgDao).successSupplier(any(String.class),any(int.class),any(String.class));
//
//	}
//
//
//
//	@Test
//	public void testTaskAdd(){
//
//
////		PowerMockito.doAnswer()
//
//
//	}
//
//}
