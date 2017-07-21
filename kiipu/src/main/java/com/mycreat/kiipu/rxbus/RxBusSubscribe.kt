package com.mycreat.kiipu.rxbus

/**
 * 用于标记观察方法的注解
 * Created by zhanghaihai on 2017/7/11.
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME) @Target(AnnotationTarget.FUNCTION)
annotation class RxBusSubscribe(val mode:ThreadMode, val isSticky:Boolean = false)
