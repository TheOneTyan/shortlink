package org.cloud.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.shortlink.project.dao.entity.ShortLinkAccessLogsDO;

/**
 * 短链接监控访问记录请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkStatsAccessRecordReqDTO extends Page<ShortLinkAccessLogsDO> {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

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
