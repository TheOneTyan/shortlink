package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.cloud.shortlink.project.dao.entity.ShortLinkLocaleStatsDO;

/**
 * 地区统计访问持久层
 */
public interface ShortLinkLocaleStatsMapper extends BaseMapper<ShortLinkLocaleStatsDO> {

    /**
     * 记录地区访问监控数据
     */
    @Insert(
            """
            INSERT INTO t_link_locale_stats (full_short_url, gid, date, cnt, country, province, city, adcode, create_time, update_time, del_flag)
            VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.gid}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE cnt = cnt + #{linkLocaleStats.cnt};
            """
    )
    void shortLinkLocaleStats(@Param("linkLocaleStats") ShortLinkLocaleStatsDO shortLinkLocaleStatsDO);
}
