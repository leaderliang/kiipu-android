package com.mycreat.kiipu.utils.bind;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by zhanghaihai on 2017/6/6.
 */
@Retention(RetentionPolicy.RUNTIME) @Target(FIELD)
public @interface BindOnclick {
}
