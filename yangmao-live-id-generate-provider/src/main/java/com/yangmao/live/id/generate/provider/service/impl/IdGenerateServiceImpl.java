package com.yangmao.live.id.generate.provider.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangmao.live.id.generate.provider.dao.mapper.IIdGenerateConfigMapper;
import com.yangmao.live.id.generate.provider.dao.po.IdGenerateConfigPO;
import com.yangmao.live.id.generate.provider.service.IIdGenerateService;
import com.yangmao.live.id.generate.provider.service.bo.LocalSeqIdBO;
import com.yangmao.live.id.generate.provider.service.bo.LocalUnSeqIdBO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
@Slf4j
@Service
public class IdGenerateServiceImpl extends ServiceImpl<IIdGenerateConfigMapper, IdGenerateConfigPO>
        implements IIdGenerateService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateServiceImpl.class);

    private static Map<Integer, LocalSeqIdBO> localSeqIdMap = new ConcurrentHashMap<>();
    private static Map<Integer, LocalUnSeqIdBO> localUnSeqIdMap = new ConcurrentHashMap<>();
    private static Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();
    private static final float UPDATE_RATE = 0.75f;
    private static final int IS_SEQ = 1;


    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000), r -> {
        Thread thread = new Thread(r);
        thread.setName("id-gen-thread-" + ThreadLocalRandom.current().nextInt(1000));
        return thread;
    });


    /**
     * 获取有序id
     */
    @Override
    public Long getSeqId(Integer id) {
        if (id == null) {
            LOGGER.error("[getSeqId] id is null");
            return null;
        }
        //id映射了一种id生成策略
        LocalSeqIdBO localSeqIdBO = localSeqIdMap.get(id);
        if (localSeqIdBO == null) {
            LOGGER.error("[getSeqId] localSeqIdBO is null, id is {}", id);
            return null;
        }
        this.refreshLocalSeqId(localSeqIdBO);
        long returnId = localSeqIdBO.getCurrentNum().getAndIncrement();
        if (returnId > localSeqIdBO.getNextThreshold()) {
            LOGGER.error("[getSeqId] returnId is over threshold, id is {}, returnId is {}, nextThreshold is {}", id,
                    returnId, localSeqIdBO.getNextThreshold());
            return null;
        }
        return returnId;
    }
    //todo

    /**
     * 获取无序id
     */
    @Override
    public Long getUnSeqId(Integer id) {
        if (id == null) {
            LOGGER.error("[getUnSeqId] id is null");
            return null;
        }
        //id映射了一种id生成策略
        LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdMap.get(id);
        if (localUnSeqIdBO == null) {
            LOGGER.error("[getUnSeqId] localUnSeqIdBO is null, id is {}", id);
            return null;
        }
        this.refreshLocalUnSeqId(localUnSeqIdBO);
        Long returnId = localUnSeqIdBO.getIdQueue().poll();
        if (returnId == null) {
            LOGGER.error("[getUnSeqId] returnId is null, id is {}", id);
            return null;
        }
        return returnId;
    }

    /**
     * 服务启动时，初始化id段
     */
    @Override
    public void afterPropertiesSet() {
        log.info("服务启动，开始初始化id段");
        List<Integer> ids = this.baseMapper.selectList(null).stream().map(IdGenerateConfigPO::getId).toList();
        for (Integer id : ids) {
            tryUpdateMysqlRecord(id);
            semaphoreMap.put(id, new Semaphore(1));
        }
    }

    private void refreshLocalUnSeqId(LocalUnSeqIdBO localUnSeqIdBO){
        int id = localUnSeqIdBO.getId();
        long step = localUnSeqIdBO.getNextThreshold() - localUnSeqIdBO.getCurrentStart();
        long use = step - localUnSeqIdBO.getIdQueue().size();
        if(use > step * UPDATE_RATE){
            Semaphore semaphore = semaphoreMap.get(id);
            if (semaphore == null) {
                LOGGER.error("semaphore is null, id is {}", id);
                return;
            }
            boolean acquireStatus = semaphore.tryAcquire();
            if (acquireStatus) {
                LOGGER.info("开始异步进行同步无序id段操作");
                threadPoolExecutor.execute(() -> {
                    try {
                        this.tryUpdateMysqlRecord(id);
                        LOGGER.info("异步进行同步无序id段操作结束");
                    }catch (Exception e){
                        LOGGER.error("[refreashLocalUnSeqId] error ",e);
                    }finally {
                        semaphore.release();
                    }
                });
            }

        }
    }

    /**
     * 更新本地有序id段
     */
    private void refreshLocalSeqId(LocalSeqIdBO localSeqIdBO) {
        long use = localSeqIdBO.getCurrentNum().get() - localSeqIdBO.getCurrentStart();
        long step = localSeqIdBO.getNextThreshold() - localSeqIdBO.getCurrentStart();
        if (use > step * UPDATE_RATE) {
            Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
            if (semaphore == null) {
                LOGGER.error("semaphore is null, id is {}", localSeqIdBO.getId());
                return;
            }
            boolean acquireStatus = semaphore.tryAcquire();
            if (acquireStatus) {
                LOGGER.info("开始异步进行同步有序id段操作");
                threadPoolExecutor.execute(() -> {
                    try {
                        this.tryUpdateMysqlRecord(localSeqIdBO.getId());
                        LOGGER.info("异步进行同步有序id段操作结束");
                    }catch (Exception e){
                        LOGGER.error("[refreashLocalSeqId] error ",e);
                    }finally {
                        //必须要在这里执行完之后再释放信号量
                        //如果释放信号量的语句在外面，实际上是将异步任务提交给线程池后立即释放了，还是会导致多线程一起执行的情况
                        semaphore.release();
                    }
                });
            }
        }
    }

    /**
     * 占用id段
     */
    private void tryUpdateMysqlRecord(Integer id) {
        for (int i = 0; i < 3; i++) {
            IdGenerateConfigPO idGenerateConfigPO = this.getById(id);
            Long nextThreshold = idGenerateConfigPO.getNextThreshold();
            Long currentStart = idGenerateConfigPO.getCurrentStart();
            boolean b = updateNewIdCountAndVersion(id);
            if (b) {
                if (idGenerateConfigPO.getIsSeq() == IS_SEQ) {
                    LocalSeqIdBO localSeqIdBO = LocalSeqIdBO.builder()
                            .currentNum(new AtomicLong(idGenerateConfigPO.getCurrentStart()))
                            .id(id)
                            .currentStart(currentStart)
                            .nextThreshold(nextThreshold)
                            .build();
                    localSeqIdMap.put(id, localSeqIdBO);
                } else {
                    ArrayList<Long> idList = new ArrayList<>();
                    for (long j = currentStart; j < nextThreshold; j++) {
                        idList.add(j);
                    }
                    Collections.shuffle(idList);
                    ConcurrentLinkedQueue<Long> idQueue = new ConcurrentLinkedQueue<>();
                    idQueue.addAll(idList);
                    LocalUnSeqIdBO localUnSeqIdBO = LocalUnSeqIdBO.builder()
                            .id(id)
                            .currentStart(currentStart)
                            .nextThreshold(nextThreshold)
                            .idQueue(idQueue)
                            .build();
                    localUnSeqIdMap.put(id, localUnSeqIdBO);
                }
                return;
            }
        }
        throw new RuntimeException("表id段占用失败，id is " + id);
    }

    /**
     * 更新mysql中分布式id的配置信息
     */
    private boolean updateNewIdCountAndVersion(Integer id) {
        IdGenerateConfigPO idGenerateConfigPO = this.getById(id);
        Integer step = idGenerateConfigPO.getStep();
        idGenerateConfigPO.setNextThreshold(idGenerateConfigPO.getNextThreshold() + step);
        idGenerateConfigPO.setCurrentStart(idGenerateConfigPO.getCurrentStart() + step);
        return this.updateById(idGenerateConfigPO);
    }

}
