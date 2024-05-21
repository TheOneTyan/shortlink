package org.cloud.shortlink.admin.controller;


import lombok.RequiredArgsConstructor;
import org.cloud.shortlink.admin.service.GroupService;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

}
