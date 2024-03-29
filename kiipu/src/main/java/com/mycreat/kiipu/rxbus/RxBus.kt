package com.mycreat.kiipu.rxbus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.ConcurrentHashMap
/**
 * PublishSubject: 只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
 *
 * Modified by zhanghaihai on 2017/7/2.
 * Created by YoKeyword on 2015/6/17.
 */
class RxBus {
    private val mBus = PublishSubject.create<Any?>().toSerialized()

    private val mStickyEventMap: MutableMap<Class<*>, Any>
    private val subscribeManager = SubscribeManager()
    init {
        mStickyEventMap = ConcurrentHashMap<Class<*>, Any>()
    }

    /**
     * 发送事件
     */
    fun post(event: Any) {
        mBus.onNext(event)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    /**
     * 判断是否有订阅者
     */
    fun hasObservers(): Boolean {
        return mBus.hasObservers()
    }

    fun reset() {
        mDefaultInstance = null
    }

    /**
     * Stciky 相关
     */

    /**
     * 发送一个新Sticky事件
     */
    fun postSticky(event: Any) {
        synchronized(mStickyEventMap) {
            mStickyEventMap.put(event.javaClass, event)
        }
        post(event)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservableSticky(eventType: Class<T>): Observable<T> {
        synchronized(mStickyEventMap) {
            val observable = mBus.ofType(eventType)
            val event = mStickyEventMap[eventType]

            if (event != null) {
                return observable.mergeWith(Observable.just(eventType.cast(event)))
            } else {
                return observable
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    fun <T> getStickyEvent(eventType: Class<T>): T {
        synchronized(mStickyEventMap) {
            return eventType.cast(mStickyEventMap[eventType])
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    fun <T> removeStickyEvent(eventType: Class<T>): T {
        synchronized(mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType))
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    fun removeAllStickyEvents() {
        synchronized(mStickyEventMap) {
            mStickyEventMap.clear()
        }
    }

    fun register(subscribeOwner:Any){
        subscribeManager.cacheSubscribeMethod(subscribeOwner)
        subscribeManager.register(subscribeOwner)
    }

    fun unregister(subscribeOwner:Any){
        subscribeManager.unRegister(subscribeOwner)
    }

    companion object {
        var mDefaultInstance: RxBus? = null
        @Synchronized fun getDefault():RxBus {
            if (mDefaultInstance == null) {
                mDefaultInstance = RxBus()
            }
            return mDefaultInstance!!
        }
    }
}