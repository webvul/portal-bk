package com.kii.beehive.portal.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.helper.IBCommonUtil;
import com.kii.beehive.business.service.ExSpaceBookService;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.exception.BusinessException;
import com.kii.beehive.portal.face.BeehiveFaceService;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.jdbc.entity.ExSitSysBeehiveUserRel;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.entity.ExSpaceBookRestBean;
import com.kii.beehive.portal.web.entity.UserRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.I18nPropertyTools;

/**
 * this class provides the web url access to industry template related functions
 */
@RestController
@RequestMapping(path = "/plugin/spaceBooking", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class ExSpaceBookController {
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ExSpaceBookService spaceBookService;

    @Autowired
    private BeehiveFaceService beehiveFaceService;

    @Autowired
    private AuthManager authManager;

    @Autowired
    private I18nPropertyTools tool;

    private Locale locale=Locale.ENGLISH;

	private Logger log= LoggerFactory.getLogger(ExSpaceBookController.class);

    private boolean debugFlag = true;

    @RequestMapping(path = "/debug", method = {RequestMethod.GET}, consumes = { "*" })
    public Object debugFlag(){
        debugFlag = !debugFlag;
        return debugFlag;
    }
    @RequestMapping(path = "/init", method = {RequestMethod.GET}, consumes = { "*" })
    public Object reInit() throws IOException {
        return spaceBookService.init();
    }

    @RequestMapping(path = "/addUserPicture", method = {RequestMethod.POST})
    public Map<String, Object> addUserPicture(@RequestBody Map<String, String> userPicture) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorcode", 0);

        try {
            BeehiveJdbcUser user = null;
            String app_code = userPicture.get("app_code");
            String campus_code = userPicture.get("campus_code");
            String user_id = userPicture.get("user_id");
            String picture_content_base64 = userPicture.get("picture_content_base64");

            if (StringUtils.isBlank(app_code)) {
                throw new IllegalArgumentException("app_code can not null");
            }
            if ( ! ExSpaceBookService.SIT_BOOKING_APP_CODE.equals(app_code)) {
                throw new IllegalArgumentException("app_code valid!");
            }
            if (StringUtils.isBlank(campus_code)) {
                throw new IllegalArgumentException("campus_code can not null");
            }

            if (StringUtils.isBlank(user_id)) {
				throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "user_id");
			}
            if (StringUtils.isBlank(picture_content_base64)) {
				throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "picture_content_base64");
			}

            //check sign
            Map<String, String> signMap = new HashMap<>();
            signMap.put("app_code", userPicture.get("app_code"));
            signMap.put("campus_code", userPicture.get("campus_code"));
            signMap.put("biz_id", userPicture.get("biz_id"));
            signMap.put("biz_type", userPicture.get("biz_type"));
            signMap.put("user_id", userPicture.get("user_id"));
            signMap.put("picture_content_base64", userPicture.get("picture_content_base64"));
            if( !debugFlag && ! IBCommonUtil.signMapKey(signMap).equals(userPicture.get("sign"))){
                result.put("errorcode", 1);
                result.put("errormsg", "sign valid!");
                return result;
            }
            //

            byte[] bytes = Base64.decodeBase64(picture_content_base64);
            File photoFile = null;
            try {
				photoFile = beehiveFaceService.createUserFaceTempFile(user_id, bytes);
			} catch (IOException e) {
				e.printStackTrace();
				throw new PortalException(ErrorCode.INVALID_INPUT, "field", "picture_content_base64");
			}
            user = beehiveFaceService.updateUserWithFace(user_id, photoFile);
            UserRestBean bean = new UserRestBean(user);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", e.getMessage());
        } catch (PortalException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", getErrorInfoInJson(e));
        }

        return result;
    }

    @RequestMapping(path = "/addUserSpaceRule", method = {RequestMethod.POST})
    public Map<String, Object> addUserSpaceRule(@RequestBody ExSpaceBookRestBean spaceBookRestBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorcode", 0);

        try {

            spaceBookRestBean.verifyInput();

            //check sign
            Map<String, String> signMap = new HashMap<>();
            signMap.put("app_code", spaceBookRestBean.getApp_code());
            signMap.put("campus_code", spaceBookRestBean.getCampus_code() );
            signMap.put("biz_id", spaceBookRestBean.getBiz_id() );
            signMap.put("biz_type", spaceBookRestBean.getBiz_type() );
            Collections.sort(spaceBookRestBean.getUserList(), new Comparator<ExSpaceBookRestBean>() {
                @Override
                public int compare(ExSpaceBookRestBean o1, ExSpaceBookRestBean o2) {
                    return (o1.getUser_id()).compareTo(o2.getUser_id());
                }
            });
            signMap.put("userList", IBCommonUtil.writeValueAsString(spaceBookRestBean.getUserList()) );
            if( !debugFlag && ! IBCommonUtil.signMapKey(signMap).equals(spaceBookRestBean.getSign())){
                result.put("errorcode", 1);
                result.put("errormsg", "sign valid!");
                return result;
            }

            spaceBookService.insertSpaceBook(spaceBookRestBean.convert2ExSpaceBook());

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", "input valid!");
        }
        checkErrorLog(spaceBookRestBean, result);
        return result;
    }

    @RequestMapping(path = "/delUserSpaceRule", method = {RequestMethod.POST})
    public Map<String, Object> delUserSpaceRule(@RequestBody ExSpaceBookRestBean spaceBookRestBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorcode", 0);
        try {

            spaceBookRestBean.verifyDelInput();

            //check sign
            Map<String, String> signMap = new HashMap<>();
            signMap.put("app_code", spaceBookRestBean.getApp_code());
            signMap.put("campus_code", spaceBookRestBean.getCampus_code() );
            signMap.put("biz_id", spaceBookRestBean.getBiz_id() );
            signMap.put("biz_type", spaceBookRestBean.getBiz_type() );
            Collections.sort(spaceBookRestBean.getUserList(), new Comparator<ExSpaceBookRestBean>() {
                @Override
                public int compare(ExSpaceBookRestBean o1, ExSpaceBookRestBean o2) {
                    return (o1.getUser_id()).compareTo(o2.getUser_id());
                }
            });
            signMap.put("userList", IBCommonUtil.writeValueAsString(spaceBookRestBean.getUserList()) );
            if( !debugFlag && ! IBCommonUtil.signMapKey(signMap).equals(spaceBookRestBean.getSign())){
                result.put("errorcode", 1);
                result.put("errormsg", "sign valid!");
                return result;
            }

            List<ExSpaceBook> spaceBookDeleted = spaceBookService.deleteSpaceBook(spaceBookRestBean.convert2ExSpaceBook());
            spaceBookService.asyncDeleteTrigger(spaceBookDeleted);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", "input valid!");
        }
        checkErrorLog(spaceBookRestBean, result);
        return result;
    }
    @RequestMapping(path = "/updateUserPassword", method = {RequestMethod.POST})
    public Map<String, Object> updateUserPassword(@RequestBody ExSpaceBookRestBean spaceBookRestBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorcode", 0);
        try {
            spaceBookRestBean.verifyUpdatePwdInput();

            //check sign
            Map<String, String> signMap = new HashMap<>();
            signMap.put("app_code", spaceBookRestBean.getApp_code());
            signMap.put("campus_code", spaceBookRestBean.getCampus_code() );
            signMap.put("biz_id", spaceBookRestBean.getBiz_id() );
            signMap.put("biz_type", spaceBookRestBean.getBiz_type() );
            signMap.put("new_password", spaceBookRestBean.getNew_password() );

            if( !debugFlag && ! IBCommonUtil.signMapKey(signMap).equals(spaceBookRestBean.getSign())){
                result.put("errorcode", 1);
                result.put("errormsg", "sign valid!");
                return result;
            }

            spaceBookService.updatePassword(spaceBookRestBean.convert2ExSpaceBookForUpdatePwd(), spaceBookRestBean.getNew_password());

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", e.getMessage());
        }
        checkErrorLog(spaceBookRestBean, result);
        return result;
    }


    @RequestMapping(path = "/getUserIdList", method = {RequestMethod.POST})
    public Map<String, Object> getUserIdList(@RequestBody ExSpaceBookRestBean spaceBookRestBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorcode", 0);
        try {

            spaceBookRestBean.verifyGetUserIdInput();

            //check sign
            Map<String, String> signMap = new HashMap<>();
            signMap.put("app_code", spaceBookRestBean.getApp_code());
            Collections.sort(spaceBookRestBean.getUserList(), new Comparator<ExSpaceBookRestBean>() {
                @Override
                public int compare(ExSpaceBookRestBean o1, ExSpaceBookRestBean o2) {
                    return (o1.getUser_id()).compareTo(o2.getUser_id());
                }
            });
            signMap.put("userList", IBCommonUtil.writeValueAsString(spaceBookRestBean.getUserList()) );
            if( !debugFlag && ! IBCommonUtil.signMapKey(signMap).equals(spaceBookRestBean.getSign())){
                result.put("errorcode", 1);
                result.put("errormsg", "sign valid!");
                return result;
            }
            //
            List<Map<String, String>> userList = new ArrayList<>();
            spaceBookRestBean.getUserList().forEach(o->{
                Map<String, String> user = new HashMap<>();
                userList.add(user);
                user.put("user_id", o.getUser_id());
                String beehiveUserId = spaceBookService.getUserIdList(o.getUser_id());
                if(StringUtils.isBlank(beehiveUserId)) {
                    BeehiveJdbcUser beehiveUser = new BeehiveJdbcUser();
                    beehiveUser.setUserName(o.getUser_id());
                    beehiveUser.setUserPassword(ExSpaceBookService.SIT_BOOKING_USER_DEFAULT_PWD);
                    beehiveUser.setRoleName("commUser");
                    Map<String, String> beehiveUserMap = authManager.createUserDirectly(beehiveUser, ExSpaceBookService.SIT_BOOKING_USER_DEFAULT_PWD);
                    beehiveUserId = beehiveUserMap.get("userID");
                    ExSitSysBeehiveUserRel exSitSysBeehiveUserRel = new ExSitSysBeehiveUserRel();
                    exSitSysBeehiveUserRel.setSit_sys_user_id(o.getUser_id());
                    exSitSysBeehiveUserRel.setBeehive_user_id(beehiveUserId);
                    spaceBookService.insertBeehiveUserRel(exSitSysBeehiveUserRel);
                }
                user.put("beehive_user_id", beehiveUserId);

            });
            result.put("userList", userList);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result.put("errorcode", 1);
            result.put("errormsg", "input valid!");
        }
        checkErrorLog(spaceBookRestBean, result);
        return result;
    }

    private void checkErrorLog(Object input, Map<String, Object> result) {
        if( ! result.get("errorcode").toString().equals("0")) {
            try {
                log.error("sit-booking-check-error:"+ result +" input:" + objectMapper.writeValueAsString(input));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private String getErrorInfoInJson(BusinessException ex) {

        String code=ex.getErrorCode();

        String fullCode=ex.getClass().getName()+"."+code;

        I18nPropertyTools.PropertyEntry  entry=tool.getPropertyEntry("error.errorMessage", locale);

        String msgTemplate=entry.getPropertyValue(fullCode);

        String  msg= StrTemplate.generByMap(msgTemplate,ex.getParamMap());

        return msg;
    }


}