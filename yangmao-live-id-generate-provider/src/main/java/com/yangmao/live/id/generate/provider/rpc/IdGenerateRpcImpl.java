package com.yangmao.live.id.generate.provider.rpc;

import com.yangmao.live.id.generate.interfaces.IdGenerateRpc;
import com.yangmao.live.id.generate.provider.service.IIdGenerateService;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
public class IdGenerateRpcImpl implements IdGenerateRpc {

    @Resource
    IIdGenerateService IIdGenerateService;

    @Override
    public Long getSeqId(Integer id) {
        return IIdGenerateService.getSeqId(id);
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return IIdGenerateService.getUnSeqId(id);
    }
}
