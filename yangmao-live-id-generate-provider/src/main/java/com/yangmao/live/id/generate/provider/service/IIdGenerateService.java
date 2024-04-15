package com.yangmao.live.id.generate.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangmao.live.id.generate.provider.dao.po.IdGenerateConfigPO;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
public interface IIdGenerateService extends IService<IdGenerateConfigPO> {

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
