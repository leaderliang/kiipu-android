package com.mycreat.kiipu.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by zhanghaihai on 2017/6/6.
 */
@Retention(RetentionPolicy.RUNTIME) @Target(FIELD)
public @interface BindView {
    /** View ID to which the field will be bound. */
    int value();
}
