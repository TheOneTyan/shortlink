package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cloud.shortlink.project.dao.entity.ShortLinkOsStatsDO;
import org.cloud.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据短链接获取指定日期内操作系统监控数据
     */
    @Select("SELECT " +
            "    os, " +
            "    SUM(cnt) AS count " +
            "FROM " +
            "    t_link_os_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, os;")
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
