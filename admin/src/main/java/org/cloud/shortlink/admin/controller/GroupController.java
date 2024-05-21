package org.cloud.shortlink.admin.controller;


import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.convention.result.Result;
import org.cloud.shortlink.admin.convention.result.Results;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.cloud.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import org.cloud.shortlink.admin.dto.resp.ShortLinkGroupListRespDTO;
import org.cloud.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/short-link/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParam){
        groupService.saveGroup(requestParam);
        return Results.success();
    }

    @GetMapping("/api/short-link/v1/group")
    public Result<List<ShortLinkGroupListRespDTO>> save(){
        return Results.success(groupService.listGroup());
    }


    @PutMapping("/api/short-link/v1/group")
    public Result<Void> update(@RequestBody ShortLinkGroupUpdateReqDTO requestParam){
        groupService.updateGroup(requestParam);
        return Results.success();
    }
}
