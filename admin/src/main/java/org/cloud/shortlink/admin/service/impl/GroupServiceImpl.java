package org.cloud.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cloud.shortlink.admin.dao.entity.GroupDO;
import org.cloud.shortlink.admin.dao.mapper.GroupMapper;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.cloud.shortlink.admin.dto.resp.ShortLinkGroupListRespDTO;
import org.cloud.shortlink.admin.service.GroupService;
import org.cloud.shortlink.admin.toolkit.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void saveGroup(ShortLinkGroupSaveReqDTO requestParam) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!availableGroupName(gid, "mading1"));

        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(requestParam.getName())
                .username("mading1")
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<ShortLinkGroupListRespDTO> listGroup() {
        //TODO 从UserContext中获取username
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, "mading1")
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupListRespDTO.class);
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
        //TODO: 替换为从UserContext中取username
        return baseMapper.selectOne(queryWrapper) == null;
    }
}
