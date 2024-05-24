package org.cloud.shortlink.admin.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.shortlink.admin.dao.entity.ShortLinkDO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page<ShortLinkDO> {
    private List<String> gidList;
}
