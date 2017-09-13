package com.mycreat.kiipu.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanghaihai on 2017/8/19.
 */
public class ReflectUtils {
    public static Field tmpField;
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

    @Nullable
    public static Field getReadableField(@NotNull Object object, String fieldName){
        if(tmpField ==  null) {

            try {
                tmpField = object.getClass().getField(fieldName);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if(tmpField != null) tmpField.setAccessible(true);
        return tmpField;
    }

}
