package com.yangmao.live.user.constants;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
public enum CacheAsyncDeleteCodeEnum {

    USER_INFO_DELETE(0,"用户基础信息删除"),
    USER_TAG_DELETE(1,"用户标签删除");

    int code;
    String desc;

    CacheAsyncDeleteCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
