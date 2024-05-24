package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.cloud.shortlink.project.dao.entity.ShortLinkAccessStatsDO;

/**
 * 短链接基础访问监控持久层
 */
public interface ShortLinkAccessStatsMapper extends BaseMapper<ShortLinkAccessStatsDO> {

    /**
     * 记录基础访问监控数据
     */
    @Insert(
        """
        INSERT INTO t_link_access_stats (full_short_url, gid, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag)
        VALUES (#{shortLinkAccessStats.fullShortUrl}, #{shortLinkAccessStats.gid}, #{shortLinkAccessStats.date}, #{shortLinkAccessStats.pv}, #{shortLinkAccessStats.uv}, #{shortLinkAccessStats.uip}, #{shortLinkAccessStats.hour}, #{shortLinkAccessStats.weekday}, NOW(), NOW(), 0) 
        ON DUPLICATE KEY UPDATE 
        pv = pv + #{shortLinkAccessStats.pv},
        uv = uv + #{shortLinkAccessStats.uv},
        uip = uip + #{shortLinkAccessStats.uip};
        """
    )
    void shortLinkAccessStats(@Param("shortLinkAccessStats") ShortLinkAccessStatsDO shortLinkAccessStatsDO);
}
