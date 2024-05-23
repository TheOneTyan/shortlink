package org.cloud.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dto.req.RecycleBinClearReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinMoveIntoReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinRecoverReqDTO;

public interface RecycleBinService extends IService<ShortLinkDO> {
    void moveIntoRecycleBin(RecycleBinMoveIntoReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void clearRecycleBin(RecycleBinClearReqDTO requestParam);
}
