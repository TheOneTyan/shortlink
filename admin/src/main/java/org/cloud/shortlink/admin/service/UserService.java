package org.cloud.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.admin.dao.entity.UserDO;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;

public interface UserService extends IService<UserDO> {

    UserDesensitizedRespDTO getDesensitizedUserByUsername(String username);

    UserSensitiveRespDTO getSensitiveUserByUsername(String username);
}
