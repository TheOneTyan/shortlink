package org.cloud.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.admin.dao.entity.UserDO;
import org.cloud.shortlink.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);
}
