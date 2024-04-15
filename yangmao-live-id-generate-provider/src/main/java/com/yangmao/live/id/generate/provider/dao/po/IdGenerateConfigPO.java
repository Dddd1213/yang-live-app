package com.yangmao.live.id.generate.provider.dao.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-04-13
 */
@Data
@TableName("t_id_generate_config")
public class IdGenerateConfigPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String remark;

    private Long nextThreshold;

    private Long initNum;

    private Long currentStart;

    private Integer step;

    private Integer isSeq;

    private String idPrefix;

    @Version
    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
