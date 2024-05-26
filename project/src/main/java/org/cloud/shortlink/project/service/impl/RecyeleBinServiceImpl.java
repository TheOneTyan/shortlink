package org.cloud.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dao.mapper.ShortLinkMapper;
import org.cloud.shortlink.project.dto.req.RecycleBinClearReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinMoveIntoReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.project.service.RecycleBinService;
import org.cloud.shortlink.project.toolkit.LinkUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.cloud.shortlink.project.common.constant.RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY;
import static org.cloud.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;

@Service
@RequiredArgsConstructor
public class RecyeleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void moveIntoRecycleBin(RecycleBinMoveIntoReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0);

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .enableStatus(1)
                .build();

        baseMapper.update(shortLinkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 1);

        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .enableStatus(0)
                .build();

        baseMapper.update(shortLinkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()),
                shortLinkDO.getOriginUrl(),
                LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void clearRecycleBin(RecycleBinClearReqDTO requestParam) {
        // TODO 试着使用更新？
        // delTime = 0L，表示没被彻底删除的链接
//        LambdaUpdateWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
//                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
//                .eq(ShortLinkDO::getGid, requestParam.getGid())
//                .eq(ShortLinkDO::getEnableStatus, 1);
//        baseMapper.delete(queryWrapper);
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 1)
                .eq(ShortLinkDO::getDelTime, 0L)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO delShortLinkDO = ShortLinkDO.builder()
                .delTime(System.currentTimeMillis())
                .build();
        delShortLinkDO.setDelFlag(1);
        baseMapper.update(delShortLinkDO, updateWrapper);
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, requestParam.getGidList())
                .eq(ShortLinkDO::getEnableStatus, 1);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

}
