package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.cloud.shortlink.project.dao.entity.ShortLinkNetworkStatsDO;
import org.cloud.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

/**
 * 访问网络监控持久层
 */
public interface ShortLinkNetworkStatsMapper extends BaseMapper<ShortLinkNetworkStatsDO> {

    /**
     * 记录访问设备监控数据
     */
    @Insert(
        """
        INSERT INTO t_link_network_stats (full_short_url, gid, date, cnt, network, create_time, update_time, del_flag)
        VALUES (#{shortLinkNetworkStats.fullShortUrl}, #{shortLinkNetworkStats.gid}, #{shortLinkNetworkStats.date}, #{shortLinkNetworkStats.cnt}, #{shortLinkNetworkStats.network}, NOW(), NOW(), 0)
        ON DUPLICATE KEY UPDATE
        cnt = cnt +  #{shortLinkNetworkStats.cnt};
        """
    )
    void shortLinkNetworkStats(@Param("shortLinkNetworkStats") ShortLinkNetworkStatsDO shortLinkNetworkStatsDO);

    /**
     * 根据短链接获取指定日期内访问网络监控数据
     */
    @Select("SELECT " +
            "    network, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_network_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, network;")
    List<ShortLinkNetworkStatsDO> listNetworkStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问网络监控数据
     */
    @Select("SELECT " +
            "    network, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_network_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, network;")
    List<ShortLinkNetworkStatsDO> listNetworkStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
