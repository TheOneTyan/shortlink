package org.cloud.shortlink.admin.controller;

import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.dto.req.RecycleBinMoveIntoReqDTO;
import org.cloud.shortlink.admin.remote.ShortLinkRemoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecycleBinController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    @PostMapping("/api/short-link/admin/v1/recycle-bin/move-into")
    public Result<Void> moveIntoRecycleBin(@RequestBody RecycleBinMoveIntoReqDTO requestParam) {
        return shortLinkRemoteService.moveIntoRecycleBin(requestParam);
    }

}
