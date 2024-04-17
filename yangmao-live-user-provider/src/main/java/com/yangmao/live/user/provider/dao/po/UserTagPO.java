package com.yangmao.live.user.provider.dao.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 用户标签记录
 * </p>
 *
 * @author author
 * @since 2024-04-16
 */
@Data
@Builder
@TableName("t_user_tag")
public class UserTagPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(type = IdType.INPUT)
    private Long userId;

    /**
     * 标签记录字段
     */
    @TableField(value = "tag_info_01")
    private Long tagInfo01;

    /**
     * 标签记录字段
     */
    @TableField(value = "tag_info_02")
    private Long tagInfo02;

    /**
     * 标签记录字段
     */
    @TableField(value = "tag_info_03")
    private Long tagInfo03;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
