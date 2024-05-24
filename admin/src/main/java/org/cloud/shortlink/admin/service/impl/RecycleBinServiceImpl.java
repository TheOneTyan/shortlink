package org.cloud.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.common.biz.user.UserContext;
import org.cloud.shortlink.admin.convention.exception.ServiceException;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.dao.entity.GroupDO;
import org.cloud.shortlink.admin.dao.mapper.GroupMapper;
import org.cloud.shortlink.admin.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.cloud.shortlink.admin.remote.ShortLinkRemoteService;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.admin.service.RecycleBinService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * URL 回收站接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};
    private final GroupMapper groupMapper;

    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return shortLinkRemoteService.pageRecycleBin(requestParam);
    }
}
