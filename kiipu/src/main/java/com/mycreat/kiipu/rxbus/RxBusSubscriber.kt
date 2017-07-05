package com.mycreat.kiipu.rxbus

import org.reactivestreams.Subscriber



/**
 * Created by zhanghaihai on 2017/7/2.
 */
abstract class RxBusSubscriber<T> : Subscriber<T> {

    override fun onNext(t: T) {
        try {
            onEvent(t)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun onCompleted() {}

    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

    protected abstract fun onEvent(t: T)
}