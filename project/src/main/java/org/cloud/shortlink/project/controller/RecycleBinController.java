package org.cloud.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.convention.result.Results;
import org.cloud.shortlink.project.dto.req.RecycleBinClearReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinMoveIntoReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import org.cloud.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.project.service.RecycleBinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    @PostMapping("/api/short-link/v1/recycle-bin/move-into")
    public Result<Void> moveToRecycleBin(@RequestBody RecycleBinMoveIntoReqDTO requestParam) {
        recycleBinService.moveIntoRecycleBin(requestParam);
        return Results.success();
    }

    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    @PostMapping("/api/short-link/v1/recycle-bin/clear")
    public Result<Void> clearRecycleBin(@RequestBody RecycleBinClearReqDTO requestParam) {
        recycleBinService.clearRecycleBin(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBin(ShortLinkRecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageRecycleBin(requestParam));
    }
}
