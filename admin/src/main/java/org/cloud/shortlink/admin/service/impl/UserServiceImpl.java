package org.cloud.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cloud.shortlink.admin.dao.entity.UserDO;
import org.cloud.shortlink.admin.dao.mapper.UserMapper;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;
import org.cloud.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserDesensitizedRespDTO getDesensitizedUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        UserDesensitizedRespDTO result = new UserDesensitizedRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public UserSensitiveRespDTO getSensitiveUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        UserSensitiveRespDTO result = new UserSensitiveRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }
}
