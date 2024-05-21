package org.cloud.shortlink.admin.filter;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.common.biz.user.UserContext;
import org.cloud.shortlink.admin.common.biz.user.UserInfoDTO;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // TODO 修改拦截路由
        String requestURI = httpServletRequest.getRequestURI();
        if (Objects.equals(requestURI, "/api/short-link/v1/user/login")) {
            return;
        }

        String token = httpServletRequest.getHeader("token");
        String username = httpServletRequest.getHeader("username");
        String loginKey = "login_" + username;
        Object userInfoStr = stringRedisTemplate.opsForHash().get(loginKey, token);
        if (userInfoStr != null) {
            UserInfoDTO userInfoDTO = JSON.parseObject(userInfoStr.toString(), UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
