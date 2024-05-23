package org.cloud.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.cloud.shortlink.admin.dao.entity.GroupDO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void createGroup(ShortLinkGroupSaveReqDTO requestParam);
    void createGroup(String groupName, String username);

    List<ShortLinkGroupRespDTO> listGroup();

    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);

    /**
     * 给分组排序
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}
