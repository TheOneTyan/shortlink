package org.cloud.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class UserLoginReqDTO {
    private String username;
    private String password;
}
