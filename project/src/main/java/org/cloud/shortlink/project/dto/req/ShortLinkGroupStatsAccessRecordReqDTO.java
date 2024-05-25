package org.cloud.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.EqualsAndHashCode;
import org.cloud.shortlink.project.dao.entity.ShortLinkAccessLogsDO;
import lombok.Data;

/**
 * 分组短链接监控访问记录请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkGroupStatsAccessRecordReqDTO extends Page<ShortLinkAccessLogsDO> {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
