package org.cloud.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;
import org.cloud.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量创建短链接返回参数
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    List<ShortLinkGroupCountRespDTO> listGroupShortLinkCount(List<String> gidList);

    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 短链接统计
     *
     * @param fullShortUrl         完整短链接
     * @param gid                  分组标识
     * @param shortLinkStatsRecord 短链接统计实体参数
     */
    void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO shortLinkStatsRecord);
}
