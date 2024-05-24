
package org.cloud.shortlink.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * URL 回收站接口层
 */
public interface RecycleBinService {

    /**
     * 分页查询回收站短链接
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
