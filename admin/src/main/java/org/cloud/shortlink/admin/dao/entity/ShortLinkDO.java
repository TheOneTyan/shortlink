package org.cloud.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.cloud.shortlink.admin.common.database.BaseDO;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link")
public class ShortLinkDO extends BaseDO {
    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识 0：已启用 1：未启用
     */
    @TableField(fill = FieldFill.INSERT)
    private int enableStatus;

    /**
     * 创建类型 0：接口 1：控制台
     */
    private int createdType;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    @TableField(value = "`describe`")
    private String describe;
}
