package com.yangmao.live.id.generate.interfaces;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
public interface IdGenerateRpc {

    /**
     * 获取有序id
     * @param id
     * @return
     */
    Long getSeqId(Integer id);

    /**
     * 获取无序id
     * @param id
     * @return
     */
    Long getUnSeqId(Integer id);
}
