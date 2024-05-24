package org.cloud.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.cloud.shortlink.project.dao.entity.ShortLinkDeviceStatsDO;

/**
 * 访问设备监控持久层
 */
public interface ShortLinkDeviceStatsMapper extends BaseMapper<ShortLinkDeviceStatsDO> {

    /**
     * 记录访问设备监控数据
     */
    @Insert(
        """
        INSERT INTO t_link_device_stats (full_short_url, gid, date, cnt, device, create_time, update_time, del_flag)
        VALUES (#{shortLinkDeviceStats.fullShortUrl}, #{shortLinkDeviceStats.gid}, #{shortLinkDeviceStats.date}, #{shortLinkDeviceStats.cnt}, #{shortLinkDeviceStats.device}, NOW(), NOW(), 0)
        ON DUPLICATE KEY UPDATE
        cnt = cnt +  #{shortLinkDeviceStats.cnt};
        """
    )
    void shortLinkDeviceStats(@Param("shortLinkDeviceStats") ShortLinkDeviceStatsDO shortLinkDeviceStatsDO);
}
