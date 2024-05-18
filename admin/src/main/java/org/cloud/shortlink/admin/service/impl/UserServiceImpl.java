package org.cloud.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.convention.exception.ClientException;
import org.cloud.shortlink.admin.dao.entity.UserDO;
import org.cloud.shortlink.admin.dao.mapper.UserMapper;
import org.cloud.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.cloud.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;
import org.cloud.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.cloud.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static org.cloud.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static org.cloud.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

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

    @Override
    public Boolean availableUsername(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (!availableUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        String lockKey = LOCK_USER_REGISTER_KEY + requestParam.getUsername();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock()) {
                int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (inserted < 1) {
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
            } else {
                throw new ClientException(USER_NAME_EXIST);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<UserDO> wrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), wrapper);
    }
}
