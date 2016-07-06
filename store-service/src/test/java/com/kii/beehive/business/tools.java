//package com.kii.beehive.business;
//
//import java.util.List;
//
//import org.junit.Test;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.test.annotation.Commit;
//import org.springframework.util.StringUtils;
//
//import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
//import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
//import com.kii.beehive.portal.service.BeehiveUserDao;
//import com.kii.beehive.portal.store.StoreServiceTestInit;
//import com.kii.beehive.portal.store.entity.BeehiveUser;
//
//
//public class Tools extends StoreServiceTestInit {
//
//
//
//	@Autowired
//	private BeehiveUserDao kiiDao;
//
//	@Autowired
//	private BeehiveUserJdbcDao  jdbcDao;
//
//
//	@Commit
//	@Test
//	public void moveUser(){
//
//		List<BeehiveUser> userList=kiiDao.getAllUsers();
//
//		userList.forEach((user)->{
//
//			BeehiveJdbcUser  jdbc=new BeehiveJdbcUser();
//			BeanUtils.copyProperties(user,jdbc);
//
//			jdbc.setUserID(user.getId());
//
//			if(StringUtils.isEmpty(jdbc.getUserPassword())){
//				jdbc.setUserPassword(jdbc.getDefaultPassword());
//			}
//
//			try {
//				jdbcDao.addUser(jdbc);
//			}catch(DuplicateKeyException e){
////				e.printStackTrace();
//			}catch(DataAccessException e){
//			e.printStackTrace();
//			}
//
//		});
//
//	}
//}
