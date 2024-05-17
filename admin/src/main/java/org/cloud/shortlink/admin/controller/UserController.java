package org.cloud.shortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.dto.resp.UserRespDTO;
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

    @GetMapping("/api/short-link/v1/user/{username}")
    public UserRespDTO getUserByUsername(@PathVariable(name = "username") String username) {
        return userService.getUserByUsername(username);
    }
}
