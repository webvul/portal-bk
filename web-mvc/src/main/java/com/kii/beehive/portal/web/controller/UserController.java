package com.kii.beehive.portal.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/users",  consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {

    /**
     * Beehive API - User API
     * 创建用户
     * POST /users
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Create User (创建用户)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.POST})
    public void createUser(@RequestBody String requestBody){
        // TODO

    }

    /**
     * Beehive API - User API
     * 更新用户
     * PUT /users
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Update User (更新用户)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.PUT})
    public void updateUser(@RequestBody String requestBody){
        // TODO

    }

    /**
     * Beehive API - User API
     * 删除用户
     * DELETE /users
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Delete User (删除用户)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.PUT})
    public void deleteUser(@RequestBody String requestBody){
        // TODO

    }

    /**
     * Beehive API - User API
     * 查询用户
     * GET /users/
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Inquire User (查询用户)" for more details
     *
     * @param requestBody
     */
    @RequestMapping(path="/",method={RequestMethod.GET})
    public void queryUser(@RequestBody String requestBody){
        // TODO

    }


}
