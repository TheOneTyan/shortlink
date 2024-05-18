package org.cloud.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * 用户返回未脱敏参数响应
 */
@Data
public class UserSensitiveRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
