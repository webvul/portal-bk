package com.kii.beehive.portal.web.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/usergroup",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserGroupController {


    /**
     * Beehive API - User API
     * 创建用户群组
     * POST /usergroup
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Create User Group (创建用户群组)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.POST})
    public void createUserGroup(@RequestBody String requestBody){
        // TODO

    }

    /**
     * Beehive API - User API
     * 更新用户群组
     * PUT /usergroup
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Update User Group (更新用户群组)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.PUT})
    public void updateUserGroup(@RequestBody String requestBody){
        // TODO

    }

    /**
     * Beehive API - User API
     * 删除用户群组
     * DELETE /usergroup
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Delete User Group (删除用户群组)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.DELETE})
    public void deleteUserGroup(@RequestBody String requestBody){
        // TODO

    }

    /**
     * Beehive API - User API
     * 查询用户群组
     * GET /usergroup
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.GET})
    public void queryUserGroup(@RequestBody String requestBody){
        // TODO

    }


}
