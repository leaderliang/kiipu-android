package com.mycreat.kiipu.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.utils.bind.BindOnclick;
import com.mycreat.kiipu.utils.bind.BindView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于简化view查找、view事件绑定操作
 * Created by zhanghaihai on 2017/6/7.
 */

public class ViewUtils {

    private static ViewUtils viewUtils;
    private Map<String, SparseArray<Field>> weakClassFieldMap;

    private ViewUtils(){
        weakClassFieldMap = new HashMap<>();
    }

    private static ViewUtils getInstance(){
        return viewUtils != null ? viewUtils:(viewUtils = new ViewUtils());
    }

    public static SparseArray<Field> bindViews(@NonNull Activity activity, @NonNull Object viewVariableOwner ) throws IllegalStateException{
        return bindViews(((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0), viewVariableOwner, null);
    }
    public static SparseArray<Field> bindViews(@NonNull View view, @NonNull Object viewVariableOwner ) throws IllegalStateException{
        return bindViews(view, viewVariableOwner, null);
    }

    public static SparseArray<Field> bindViews(@NonNull View view, @NonNull Object viewVariableOwner, SparseArray<Field> bindMap ) throws IllegalStateException{
        ViewUtils viewUtils = getInstance();
        String ownerClassName = viewVariableOwner.getClass().getName();

        if(bindMap == null && viewUtils.weakClassFieldMap.containsKey(ownerClassName))
            bindMap = viewUtils.weakClassFieldMap.get(ownerClassName);

        if(bindMap != null && bindMap.size() > 0){//已经缓存可匹配view的Field, 直接使用
            for(int i =0 ; i < bindMap.size(); i++ ){
                viewUtils.bindView(bindMap.valueAt(i), viewVariableOwner, view);
            }
        }else {
            bindMap = new SparseArray<>();
            Field[] fields = viewVariableOwner.getClass().getDeclaredFields();
            for (Field field : fields) {
                int id = viewUtils.bindView(field, viewVariableOwner, view);
                if (id > 0) {
                    bindMap.put(id, field);
                }
            }
            cacheFieldMap(ownerClassName, bindMap);
        }
        return bindMap;
    }

    private int bindView(Field field, Object viewVariableOwner, View view){
        try {
            field.setAccessible(true);
            Class fc = field.getType();
            //非View直接返回
            if(!View.class.isAssignableFrom(fc)) return 0;
            int id;
            //根据给定注解中的名字Bind
            if(field.isAnnotationPresent(BindView.class)){
                id  = field.getAnnotation(BindView.class).value();
            }else {//如果不给定注解，如果名字与其匹配也可以
                id = KiipuApplication.appContext.getResources().getIdentifier(field.getName(), "id", KiipuApplication.appContext.getPackageName());
            }

            if(id <= 0){//java小骆驼命名规则转下划线命名规则进行查找对应view id
                String lineName = StringUtils.humpToLine(field.getName());
                id = KiipuApplication.appContext.getResources().getIdentifier(lineName, "id", KiipuApplication.appContext.getPackageName());
            }

            if(id > 0) {
                View bindView = view.findViewById(id);
                //需判断类型是否匹配
                if(bindView != null && field.getType().isAssignableFrom(bindView.getClass())) {
                    field.set(viewVariableOwner, bindView);
                    if(field.isAnnotationPresent(BindOnclick.class) && viewVariableOwner instanceof View.OnClickListener){
                        bindView.setOnClickListener((View.OnClickListener) viewVariableOwner);
                    }else if(field.isAnnotationPresent(BindOnclick.class)){
                        //如果指定了 BindOnclick 注解, 你的 viewVariableOwner  必须继承 View.OnClickListener
                        throw new IllegalArgumentException(new Throwable("Your argument viewVariableOwner(" + viewVariableOwner.getClass().getName() +
                                ") should implements View.OnClickListener, because you have set the BindOnclick annotation to" + field.getName()));
                    }
                    return id;
                }else if(bindView != null){
                    throw new IllegalStateException(new Throwable("Filed type is not match the view type"));
                }else{
                    throw new IllegalStateException(new Throwable("Can not found "+ field.getType() + " with id(" + id + ") in view" ));
                }

            }else{
                LogUtil.e(field.getName() + " is not bind to view successfully, it may cause java.lang.NullPointException!");
                return 0;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
            return 0;
        }
    }

    private static void cacheFieldMap(Object owner, SparseArray<Field> bindMap){
        getInstance().weakClassFieldMap.put(owner.getClass().getName(), new WeakReference<>(bindMap).get());
    }

    private SparseArray<Field> getFieldBindMap(Object owner){
        SparseArray<Field> bindMap;
        if(!weakClassFieldMap.containsKey(owner.getClass().getName()))
            return null;
        else{
            bindMap = weakClassFieldMap.get(owner.getClass().getName());
            if(bindMap.size() == 0){
                return null;
            }else{
                return bindMap;
            }
        }

    }

}
