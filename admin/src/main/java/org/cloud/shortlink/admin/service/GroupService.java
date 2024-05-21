package org.cloud.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.admin.dao.entity.GroupDO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.cloud.shortlink.admin.dto.resp.ShortLinkGroupListRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(ShortLinkGroupSaveReqDTO requestParam);

    List<ShortLinkGroupListRespDTO> listGroup();
}
