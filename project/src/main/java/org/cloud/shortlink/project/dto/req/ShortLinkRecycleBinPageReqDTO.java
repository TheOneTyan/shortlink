package org.cloud.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cloud.shortlink.project.dao.entity.ShortLinkDO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page<ShortLinkDO> {
    private List<String> gidList;
}
