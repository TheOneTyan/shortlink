package org.cloud.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cloud.shortlink.project.dao.entity.ShortLinkStatsTodayDO;
import org.cloud.shortlink.project.dao.mapper.ShortLinkStatsTodayMapper;
import org.cloud.shortlink.project.service.ShortLinkStatsTodayService;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkStatsTodayServiceImpl extends ServiceImpl<ShortLinkStatsTodayMapper, ShortLinkStatsTodayDO> implements ShortLinkStatsTodayService {
}
