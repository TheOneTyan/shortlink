package org.cloud.shortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.project.convention.result.Results;
import org.cloud.shortlink.project.convention.result.Result;
import org.cloud.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.cloud.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import org.cloud.shortlink.project.service.ShortLinkStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {

    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }
}
