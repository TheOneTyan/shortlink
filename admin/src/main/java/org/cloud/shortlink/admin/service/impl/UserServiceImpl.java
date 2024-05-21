package org.cloud.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.convention.exception.ClientException;
import org.cloud.shortlink.admin.dao.entity.UserDO;
import org.cloud.shortlink.admin.dao.mapper.UserMapper;
import org.cloud.shortlink.admin.dto.req.UserLoginReqDTO;
import org.cloud.shortlink.admin.dto.req.UserRegisterReqDTO;
import org.cloud.shortlink.admin.dto.req.UserUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.UserDesensitizedRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserLoginRespDTO;
import org.cloud.shortlink.admin.dto.resp.UserSensitiveRespDTO;
import org.cloud.shortlink.admin.service.UserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.cloud.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static org.cloud.shortlink.admin.common.enums.UserErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

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

    /**
     * Hash:
     *  Key: login_{username}
     *  Value:
     *   Key: token
     *   Value: JSON (用户信息)
     */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword());
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(USER_NULL);
        }
        String loginKey = "login_" + requestParam.getUsername();
        Boolean hasLogin = stringRedisTemplate.hasKey(loginKey);
        if (hasLogin != null && hasLogin) {
            throw new ClientException(USER_HAS_LOGIN);
        }

        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(loginKey, token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(loginKey, 30L, TimeUnit.DAYS);
        return new UserLoginRespDTO(token);
    }


    /**
     *
     * @return
     * true -> has logged 已登录
     * false -> 未登录
     */
    @Override
    public Boolean hasLogged(String username, String token) {
        String loginKey = "login_" + username;
        return stringRedisTemplate.opsForHash().get(loginKey, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if (hasLogged(username, token)) {
            String loginKey = "login_" + username;
            stringRedisTemplate.delete(loginKey);
        } else {
            throw new ClientException("用户未登录或登录已过期");
        }
    }
}
