package org.cloud.shortlink.admin.filter;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.common.biz.user.UserContext;
import org.cloud.shortlink.admin.common.biz.user.UserInfoDTO;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
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
