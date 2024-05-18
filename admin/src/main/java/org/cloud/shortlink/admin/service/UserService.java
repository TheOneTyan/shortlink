package org.cloud.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.admin.dao.entity.UserDO;
import org.cloud.shortlink.admin.dto.req.UserLoginReqDTO;
import org.cloud.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.cloud.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;

public interface UserService extends IService<UserDO> {

    UserDesensitizedRespDTO getDesensitizedUserByUsername(String username);

    UserSensitiveRespDTO getSensitiveUserByUsername(String username);

    Boolean availableUsername(String username);

    void register(UserRegisterReqDTO requestParam);

    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     * @param requestParam username password
     * @return token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean hasLogged(String username, String token);

    void logout(String username, String token);
}
