package com.yangmao.live.common;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-02
 */
public class ConvertBeanUtils {

    /**
     * 将一个对象转为目标对象
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        T target = newInstance(clazz);
        BeanUtils.copyProperties(source,target);
        return target;
    }

   public static <K,T> List<T> convertList(List<K> sourceList, Class<T> targetClass){
        if(sourceList == null){
            return null;
        }
        return sourceList.stream()
                .map(source -> convert(source,targetClass))
                .collect(Collectors.toList());
   }


    private static <T> T newInstance(Class<T> tClass){
        try {
            return tClass.newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException(tClass,"instantiation error",e);
        }

    }
}
