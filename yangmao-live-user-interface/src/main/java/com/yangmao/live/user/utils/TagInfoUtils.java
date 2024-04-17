package com.yangmao.live.user.utils;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
public class TagInfoUtils {
    public static boolean isContain(Long tagInfo, Long matchTag){
        if(tagInfo==null || matchTag==null){
            return false;
        }
        return (tagInfo & matchTag) == matchTag;
    }

}
