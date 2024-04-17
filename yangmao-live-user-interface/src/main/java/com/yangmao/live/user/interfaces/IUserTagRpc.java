package com.yangmao.live.user.interfaces;

import com.yangmao.live.user.constants.UserTagsEnum;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
public interface IUserTagRpc {

   /**
    * 设置标签
    *
    * @param userId
    * @param userTagsEnum
    * @return
    */
   boolean setTag(Long userId, UserTagsEnum userTagsEnum);

   /**
    * 取消标签
    *
    * @param userId
    * @param userTagsEnum
    * @return
    */
   boolean cancelTag(Long userId,UserTagsEnum userTagsEnum);

   /**
    * 是否包含某个标签
    *
    * @param userId
    * @param userTagsEnum
    * @return
    */
   boolean containTag(Long userId, UserTagsEnum userTagsEnum);
}
