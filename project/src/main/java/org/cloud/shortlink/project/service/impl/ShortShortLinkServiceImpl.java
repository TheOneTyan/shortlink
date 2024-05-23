package org.cloud.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.project.convention.exception.ServiceException;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dao.entity.ShortLinkGotoDo;
import org.cloud.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import org.cloud.shortlink.project.dao.mapper.ShortLinkMapper;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.project.service.ShortLinkService;
import org.cloud.shortlink.project.toolkit.HashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.cloud.shortlink.project.common.constant.RedisKeyConstant.*;

@Service
@RequiredArgsConstructor
public class ShortShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortUri = generateShortUri(requestParam);
        // TODO 从原始链接中获取 协议字段
        String fullShortUrl = "https://" +
                requestParam.getDomain() +
                "/" +
                shortUri;

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortUri)
                .enableStatus(0)
                .fullShortUrl(fullShortUrl)
                .build();

        ShortLinkGotoDo shortLinkGotoDo = ShortLinkGotoDo.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDo);
            stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl), requestParam.getOriginUrl());
        } catch (DuplicateKeyException ex) {
            throw new ServiceException("短链接已存在，请勿重复生成");
        }

        shortLinkCreateCachePenetrationBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountRespDTO> listGroupShortLinkCount(List<String> gidList) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid", "count(*) as shortLinkCount")
                .in("gid", gidList)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> resultMaps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(resultMaps, ShortLinkGroupCountRespDTO.class);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 和示例不同，我不允许修改gid，所以不需要先删后插
        // TODO 在【功能扩展@短链接变更分组记录功能】章节后补全更新短链接功能
    }

    /**
     * 根据短链接跳转至原链接
     * @param shortUri 6位短链接
     */
    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        String domain = request.getServerName();
        // TODO 获取原始链接中的协议字段
        String fullShortUrl = "https://" +
                domain +
                "/" +
                shortUri;
        String originUrl = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originUrl)) {
            try {
                response.sendRedirect(originUrl);
            } catch (IOException e) {
                throw new ServiceException("跳转失败");
            }
            return;
        }

        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            originUrl = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originUrl)) {
                try {
                    response.sendRedirect(originUrl);
                } catch (IOException e) {
                    throw new ServiceException("跳转失败");
                }
                return;
            }

            LambdaQueryWrapper<ShortLinkGotoDo> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDo.class)
                    .eq(ShortLinkGotoDo::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDo shortLinkGotoDo = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDo == null) {
                throw new ServiceException("此完整短链接在路由表中不存在，需要重新插入");
            }

            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDo.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(linkQueryWrapper);
            if (shortLinkDO == null) {
                throw new ServiceException("此短链接不存在于数据库中");
            }

            try {
                response.sendRedirect(shortLinkDO.getOriginUrl());
            } catch (IOException e) {
                throw new ServiceException("跳转失败");
            }
        } finally {
            lock.unlock();
        }

    }

    private String generateShortUri(ShortLinkCreateReqDTO requestParam) {
        String shortUri = "";
        int maxTryCount = 10;
        while (true) {
            if (maxTryCount <= 0) {
                throw new ServiceException("短链接生成过于频繁，稍后重试");
            }
            // 防止高并发时生成相同短链接并插入
            String originUrlAndUuid = requestParam.getOriginUrl() + UUID.randomUUID();
            shortUri = HashUtil.hashToBase62(originUrlAndUuid);
            String fullShortUrl = requestParam.getDomain() + "/" + shortUri;
            // 布隆过滤器：不存在则一定不存在，此时生成的shortUri满足要求
            if (!shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                break;
            }
            maxTryCount--;
        }
        return shortUri;
    }

    private String parseProtocolFromUrl(String url) {
        if (url.startsWith("http://")) {
            return "http://";
        } else if (url.startsWith("https://")) {
            return "https://";
        } else {
            return "";
        }
    }
}
