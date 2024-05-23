package org.cloud.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cloud.shortlink.admin.common.biz.user.UserContext;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.dao.entity.GroupDO;
import org.cloud.shortlink.admin.dao.mapper.GroupMapper;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import org.cloud.shortlink.admin.remote.ShortLinkRemoteService;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.admin.service.GroupService;
import org.cloud.shortlink.admin.toolkit.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {};

    @Override
    public void createGroup(ShortLinkGroupSaveReqDTO requestParam) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!availableGroupName(gid, UserContext.getUsername()));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(requestParam.getName())
                .username(UserContext.getUsername())
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public void createGroup(String groupName, String username) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!availableGroupName(gid, UserContext.getUsername()));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .username(username)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groups = baseMapper.selectList(queryWrapper);
        Result<List<ShortLinkGroupCountRespDTO>> listGroupCount = shortLinkRemoteService.listGroupCount(groups.stream().map(GroupDO::getGid).toList());
        List<ShortLinkGroupRespDTO> results = BeanUtil.copyToList(groups, ShortLinkGroupRespDTO.class);
        return results.stream().peek(each -> listGroupCount.getData().stream()
                .filter(count -> count.getGid().equals(each.getGid()))
                .findFirst()
                .ifPresent(count -> each.setShortLinkCount(count.getShortLinkCount()))).collect(Collectors.toList());
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> deleteWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername());
        baseMapper.delete(deleteWrapper);
    }

    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .set(GroupDO::getSortOrder, each.getSortOrder())
                    .eq(GroupDO::getGid, each.getGid());
            update(updateWrapper);
        });
    }

    /**
     * @return
     * true -> 可用
     * false ->不可用
     */
    private boolean availableGroupName(String gid, String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username);
        return baseMapper.selectOne(queryWrapper) == null;
    }
}
