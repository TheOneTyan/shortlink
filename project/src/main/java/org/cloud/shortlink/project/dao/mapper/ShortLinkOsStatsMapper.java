package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.cloud.shortlink.project.dao.entity.ShortLinkOsStatsDO;

/**
 * 操作系统统计访问持久层
 */
public interface ShortLinkOsStatsMapper extends BaseMapper<ShortLinkOsStatsDO> {

    /**
     * 记录地区访问监控数据
     */
    @Insert(
        """
        INSERT INTO t_link_os_stats (full_short_url, gid, date, cnt, os, create_time, update_time, del_flag)
        VALUES (#{shortLinkOsStats.fullShortUrl}, #{shortLinkOsStats.gid}, #{shortLinkOsStats.date}, #{shortLinkOsStats.cnt}, #{shortLinkOsStats.os}, NOW(), NOW(), 0)
        ON DUPLICATE KEY UPDATE
        cnt = cnt +  #{shortLinkOsStats.cnt};
        """
    )
    void shortLinkOsStats(@Param("shortLinkOsStats") ShortLinkOsStatsDO linkOsStatsDO);
}
