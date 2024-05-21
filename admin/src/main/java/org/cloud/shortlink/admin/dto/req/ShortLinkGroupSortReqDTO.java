package org.cloud.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class ShortLinkGroupSortReqDTO {
    private String gid;
    private Integer sortOrder;
}
