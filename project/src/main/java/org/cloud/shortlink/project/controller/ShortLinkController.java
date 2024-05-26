package org.cloud.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.project.convention.result.Result;
import org.cloud.shortlink.project.convention.result.Results;
import org.cloud.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.cloud.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.cloud.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @GetMapping("{short-uri}")
    public Result<Void> restoreUrl(@PathVariable(name = "short-uri") String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        shortLinkService.restoreUrl(shortUri, request, response);
        return Results.success();
    }

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/link")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam){
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/v1/link/create/batch")
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam) {
        return Results.success(shortLinkService.batchCreateShortLink(requestParam));
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/v1/link/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/link/page")
    public Result<IPage<ShortLinkPageRespDTO>> page(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/link/count")
    public Result<List<ShortLinkGroupCountRespDTO>> listGroupCount(@RequestParam("gidList") List<String> gidList) {
        return Results.success(shortLinkService.listGroupShortLinkCount(gidList));
    }
}
