package org.cloud.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.cloud.shortlink.admin.common.database.BaseDO;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName("t_link_stats_today")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsTodayDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 今日pv
     */
    private Integer todayPv;

    /**
     * 今日uv
     */
    private Integer todayUv;

    /**
     * 今日ip数
     */
    private Integer todayUip;
}
