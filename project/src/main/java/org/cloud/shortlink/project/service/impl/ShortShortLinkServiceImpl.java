package org.cloud.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dao.mapper.LinkMapper;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.project.service.ShortLinkService;
import org.cloud.shortlink.project.toolkit.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class ShortShortLinkServiceImpl extends ServiceImpl<LinkMapper, ShortLinkDO> implements ShortLinkService {
    @Override
    public ShortLinkCreateRespDTO saveLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkUri = generateShortLink(requestParam.getOriginUrl());
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkUri;
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkUri);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .build();
    }

    private String generateShortLink(String originUrl) {
        return HashUtil.hashToBase62(originUrl);
    }
}
