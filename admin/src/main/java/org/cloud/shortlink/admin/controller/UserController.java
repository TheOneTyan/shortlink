package org.cloud.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.convention.result.Results;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;
import org.cloud.shortlink.admin.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/short-link/v1/user/desensitized/{username}")
    public Result<UserDesensitizedRespDTO> getDesensitizedUserByUsername(@PathVariable(name = "username") String username) {
        return Results.success(userService.getDesensitizedUserByUsername(username));
    }

    @GetMapping("/api/short-link/v1/user/sensitive/{username}")
    public Result<UserSensitiveRespDTO> getSensitiveUserByUsername(@PathVariable(name = "username") String username) {
        return Results.success(userService.getSensitiveUserByUsername(username));
    }

}
