package org.cloud.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.remote.ShortLinkRemoteService;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShortLinkController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    @PostMapping("/api/short-link/admin/v1/link")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/link/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(@RequestBody ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }
}
