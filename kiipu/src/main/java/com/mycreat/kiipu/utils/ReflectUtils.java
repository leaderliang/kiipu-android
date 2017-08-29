package com.mycreat.kiipu.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanghaihai on 2017/8/19.
 */
public class ReflectUtils {

    /**
     * 获取所有指定对象的变量名称和值的对应关系Map
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> getAllFieldMap(@NotNull Object object) throws IllegalAccessException {
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            fieldMap.put(field.getName(), field.get(object));
        }
        return fieldMap;
    }
}
