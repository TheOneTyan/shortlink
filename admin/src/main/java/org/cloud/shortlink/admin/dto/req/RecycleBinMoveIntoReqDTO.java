package org.cloud.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class RecycleBinMoveIntoReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
