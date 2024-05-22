package org.cloud.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO saveLink(ShortLinkCreateReqDTO requestParam);
}
