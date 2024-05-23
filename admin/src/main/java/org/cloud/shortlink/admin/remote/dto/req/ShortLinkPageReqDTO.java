package org.cloud.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.shortlink.admin.dao.entity.ShortLinkDO;

/**
 * 短链接分页请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

     // super: current, size

    /**
     * 分组标识
     */
    private String gid;

}
