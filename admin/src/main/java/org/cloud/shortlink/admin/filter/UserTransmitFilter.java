package org.cloud.shortlink.admin.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.common.biz.user.UserContext;
import org.cloud.shortlink.admin.common.biz.user.UserInfoDTO;
import org.cloud.shortlink.admin.convention.exception.ClientException;
import org.cloud.shortlink.admin.convention.result.Results;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.cloud.shortlink.admin.common.enums.UserErrorCodeEnum.USER_TOKEN_FAIL;

@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/user"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!IGNORE_URI.contains(requestURI)) {
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            if (!StrUtil.isAllNotBlank(username, token)) {
                try {
                    returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            Object userInfoJsonStr;
            try {
                userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
                if (userInfoJsonStr == null) {
                    throw new ClientException(USER_TOKEN_FAIL);
                }
            } catch (Exception ex) {
                try {
                    returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
            // userInfoStr中没有token字段，所以需要手动给UserInfoDTO设置token
            userInfoDTO.setToken(token);
            UserContext.setUser(userInfoDTO);
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {

        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
