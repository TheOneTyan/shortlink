package org.cloud.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.cloud.shortlink.admin.common.database.BaseDO;

import java.util.Date;

/**
 * 操作系统统计访问实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_link_os_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkOsStatsDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 操作系统
     */
    private String os;
}
