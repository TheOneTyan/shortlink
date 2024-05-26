package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cloud.shortlink.project.dao.entity.ShortLinkBrowserStatsDO;
import org.cloud.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据短链接获取指定日期内浏览器监控数据
     */
    @Select("SELECT " +
            "    browser, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_browser_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, browser;")
    List<HashMap<String, Object>> listBrowserStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内浏览器监控数据
     */
    @Select("SELECT " +
            "    browser, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_browser_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, browser;")
    List<HashMap<String, Object>> listBrowserStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
