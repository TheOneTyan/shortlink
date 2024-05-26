package org.cloud.shortlink.admin.common.enums;

import org.cloud.shortlink.admin.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {

    USER_TOKEN_FAIL("A000200", "用户Token验证失败"),

    USER_NULL("B000200", "用户记录不存在"),
    USER_NAME_EXIST("B000201", "用户名已存在"),

    USER_EXIST("B000202", "用户记录已存在"),
    USER_SAVE_ERROR("B000203", "用户新增记录失败"),
    USER_HAS_LOGIN("B000204", "用户已登录");

    private final String code;
    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
