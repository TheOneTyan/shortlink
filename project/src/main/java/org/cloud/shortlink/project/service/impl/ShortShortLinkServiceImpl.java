package org.cloud.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.project.convention.exception.ServiceException;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dao.mapper.LinkMapper;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.project.service.ShortLinkService;
import org.cloud.shortlink.project.toolkit.HashUtil;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShortShortLinkServiceImpl extends ServiceImpl<LinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO saveLink(ShortLinkCreateReqDTO requestParam) {
        String shortUri = generateShortUri(requestParam);
        String fullShortUrl = requestParam.getDomain() +
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

        try {
            baseMapper.insert(shortLinkDO);
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
}
