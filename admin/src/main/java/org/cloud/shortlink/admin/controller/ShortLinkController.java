package org.cloud.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.convention.result.Results;
import org.cloud.shortlink.admin.remote.ShortLinkRemoteService;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.admin.toolkit.EasyExcelWebUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShortLinkController {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};

    @PostMapping("/api/short-link/admin/v1/link")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    @GetMapping("/api/short-link/admin/v1/link/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/admin/v1/link/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/api/short-link/admin/v1/link/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = shortLinkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }
}
