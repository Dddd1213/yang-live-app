package com.yangmao.live.id.generate.provider.service.bo;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Builder;
import lombok.Data;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
@Data
@Builder
public class LocalSeqIdBO {

    private Integer id;

    /**
     * 在内存中记录当前有序id的值
     */
    private AtomicLong currentNum;

    /**
     * 当前id段的结束值
     */
    private Long nextThreshold;

    /**
     * 当前id段的开始值
     */
    private Long currentStart;

}
