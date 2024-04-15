package com.yangmao.live.id.generate.provider.service.bo;

import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.Builder;
import lombok.Data;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-15
 */
@Data
@Builder
public class LocalUnSeqIdBO {

    private int id;

    private ConcurrentLinkedQueue<Long> idQueue;

    /**
     * 当前id段的结束值
     */
    private Long nextThreshold;

    /**
     * 当前id段的开始值
     */
    private Long currentStart;

}
