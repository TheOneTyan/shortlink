package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.cloud.shortlink.project.dao.entity.ShortLinkBrowserStatsDO;

/**
 * 浏览器统计访问持久层
 */
public interface ShortLinkBrowserStatsMapper extends BaseMapper<ShortLinkBrowserStatsDO> {

    /**
     * 记录浏览器访问监控数据
     */
    @Insert(
        """
        INSERT INTO t_link_browser_stats (full_short_url, gid, date, cnt, browser, create_time, update_time, del_flag)
        VALUES (#{shortLinkBrowserStats.fullShortUrl}, #{shortLinkBrowserStats.gid}, #{shortLinkBrowserStats.date}, #{shortLinkBrowserStats.cnt}, #{shortLinkBrowserStats.browser}, NOW(), NOW(), 0)
        ON DUPLICATE KEY UPDATE
        cnt = cnt +  #{shortLinkBrowserStats.cnt};
        """
    )
    void shortLinkBrowserStats(@Param("shortLinkBrowserStats") ShortLinkBrowserStatsDO linkBrowserStatsDO);
}
