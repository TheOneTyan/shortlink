package org.cloud.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dto.req.RecycleBinClearReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinMoveIntoReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService extends IService<ShortLinkDO> {
    void moveIntoRecycleBin(RecycleBinMoveIntoReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void clearRecycleBin(RecycleBinClearReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam);
}
