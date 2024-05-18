package org.cloud.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.convention.result.Results;
import org.cloud.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.cloud.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;
import org.cloud.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名查询用户 脱敏信息
     */
    @GetMapping("/api/short-link/v1/user/desensitized/{username}")
    public Result<UserDesensitizedRespDTO> getDesensitizedUserByUsername(@PathVariable(name = "username") String username) {
        return Results.success(userService.getDesensitizedUserByUsername(username));
    }

    /**
     * 根据用户名查询用户 未脱敏信息
     */
    @GetMapping("/api/short-link/v1/user/sensitive/{username}")
    public Result<UserSensitiveRespDTO> getSensitiveUserByUsername(@PathVariable(name = "username") String username) {
        return Results.success(userService.getSensitiveUserByUsername(username));
    }

    /**
     * 查询用户名可用性
     */
    @GetMapping("/api/short-link/v1/user/available-username")
    public Result<Boolean> availableUsername(@RequestParam(name = "username") String username) {
        return Results.success(userService.availableUsername(username));
    }

    /**
     * 注册新用户
     */
    @PostMapping("/api/short-link/v1/user/")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/api/short-link/v1/user/")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }
}
