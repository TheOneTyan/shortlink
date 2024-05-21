package org.cloud.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cloud.shortlink.admin.dao.entity.GroupDO;
import org.cloud.shortlink.admin.dao.mapper.GroupMapper;
import org.cloud.shortlink.admin.service.GroupService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

}
