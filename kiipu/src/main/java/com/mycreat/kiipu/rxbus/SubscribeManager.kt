package com.mycreat.kiipu.rxbus

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList

/**
 * 订阅管理
 * Created by zhanghaihai on 2017/7/11.
 */
class SubscribeManager {
    var classSubMethodMap:HashMap<Class<*>, ArrayList<SubscribeMethod>> = HashMap()
    var registeredSubscribeMap:HashMap<Class<*>, EventRegisterController> = HashMap()
    val registerLock = ReentrantLock()
    /**
     * 缓存类中包含的订阅事件，是类及方法的缓存，不会缓存对象的注册状态
     */
    fun cacheSubscribeMethod(subscribeOwner:Any):List<SubscribeMethod>?{
        val methods = subscribeOwner.javaClass.declaredMethods
        val subMethods:ArrayList<SubscribeMethod> = ArrayList()
        if(!classSubMethodMap.containsKey(subscribeOwner.javaClass))
            methods.forEach { method ->
                method?.isAccessible = true

                if(method != null && method.isAnnotationPresent(RxBusSubscribe::class.java)){
                    val rxBusSubscribe = method.getAnnotation(RxBusSubscribe::class.java)
                    val subMethod = SubscribeMethod()
                    subMethod.method = method
                    subMethod.subMode = rxBusSubscribe.mode
                    subMethod.isSticky = rxBusSubscribe.isSticky
                    subMethod.event = if(method.parameterTypes.isNotEmpty())
                        method.parameterTypes[0] else null
                    subMethods.add(subMethod)
                }

                if(subMethods.isNotEmpty()) {
                    val subs = WeakReference<ArrayList<SubscribeManager.SubscribeMethod>>(subMethods).get()
                    if(subs != null) {
                        classSubMethodMap.put(subscribeOwner.javaClass, subs)
                    }
                }

        }

        return if(subMethods.isEmpty()) null else subMethods
    }

    /**
     * 注册订阅者
     * @param subscribeOwner 订阅者宿主，可包含多个订阅者
     */
    fun register(subscribeOwner:Any){
        var subMethodList:List<SubscribeMethod>? = null
        if(classSubMethodMap.containsKey(subscribeOwner.javaClass)){
            subMethodList = classSubMethodMap[subscribeOwner.javaClass]
        }else{
            subMethodList = cacheSubscribeMethod(subscribeOwner)
        }
        if(subMethodList != null) {
            subMethodList.forEach { subMethod->
                registerLock.lock() //

                var controller:EventRegisterController? = EventRegisterController()
                //根据注册事件类型
                if(registeredSubscribeMap.containsKey(subMethod.event)) {
                    controller = registeredSubscribeMap[subMethod.event]
                }


                val disposable = addSubMethodToRxBus(subMethod, subscribeOwner)
                subMethod.disposable = disposable
                controller?.registerMap?.put(subscribeOwner.toString(), subMethod)
                registerLock.unlock()
            }
        }else{
            throw IllegalArgumentException("No Subscribe method is found in ${subscribeOwner::class.java.name}")
        }

    }

    /**
     * 注册方法及其拥有者到RxBus
     */
    fun addSubMethodToRxBus(subMethod:SubscribeMethod, subscribeOwner: Any):Disposable?{
        var mSub:Disposable? = null
        if(subMethod.event != null) {
            mSub = subscribeOn(subMethod).map(MFunction()).subscribe(MRxBusSubscriber(subMethod, subscribeOwner))
            RxCompositeDisposable.add(mSub)
        }
        return mSub

    }

    class MFunction : Function<Any, Any>{
        override fun apply(t: Any): Any {
            return t
        }

    }

    /**
     * 鉴于类似于 eventBus 每个订阅者只会有单一消息处理方法
     */
    class MRxBusSubscriber(val subMethod:SubscribeMethod, val subscribeOwner: Any) : Consumer<Any>{

        override fun accept(t: Any) {
            if(t.javaClass.isAssignableFrom(subMethod.event)){
                subMethod.method?.isAccessible = true //开启方法调用权限
                subMethod.method?.invoke(subscribeOwner, t) //调用该方法，并将值传入
            }
        }

    }

    /**
     * 根据注解中指定的MODE分配不同的Observable
     */
    fun subscribeOn(subMethod:SubscribeMethod):Observable<out Any>{
        var mSub: Observable<out Any> = if(subMethod.isSticky) RxBus.getDefault().toObservableSticky(subMethod.event!!) else  RxBus.getDefault().toObservable(subMethod.event!!)
        when(subMethod.subMode){
            ThreadMode.MAIN ->{
                mSub = mSub.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            }
            ThreadMode.COMPUTATION ->{
                mSub = mSub.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())
            }
            ThreadMode.IO ->{
                mSub = mSub.subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
            }
            ThreadMode.NEW_THREAD ->{
                mSub = mSub.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread())
            }

        }

        return mSub
    }

    /**
     * 清空指定对象的注册信息
     */
    fun unRegister(subscribeOwner:Any){
        var subMethodList:List<SubscribeMethod>? = null
        if(classSubMethodMap.containsKey(subscribeOwner.javaClass)){
            subMethodList = classSubMethodMap[subscribeOwner.javaClass]
        }else{
            subMethodList = cacheSubscribeMethod(subscribeOwner)
        }

        if(subMethodList != null) {
            registerLock.lock() //
            subMethodList.forEach { subMethod ->
                val controller = registeredSubscribeMap.get(subMethod.event)
                val disposable = controller?.registerMap?.get(subscribeOwner.toString())?.disposable
                RxCompositeDisposable.remove(disposable)
                disposable?.dispose()
                controller?.registerMap?.remove(subscribeOwner.toString())
            }
            registerLock.unlock() //
        }else{
            throw IllegalArgumentException("At last one method should have ${RxBusSubscribe::class.java} annotation!")
        }
    }

    /**
     * 保存订阅方法信息
     */
    class SubscribeMethod{
        var event:Class<out Any>? = null
        var method:Method? = null
        var subMode:ThreadMode = ThreadMode.IMMEDIATE // 默认是当前线程
        var observableMode:ThreadMode = ThreadMode.IO // TODO 暂时没有使用, 用于控制订阅后需要的中间处理过程
        var isSticky = false
        var disposable:Disposable? = null
    }

    /**
     * 注册对象控制器，保存对象与注册的订阅者关系
     */
    class EventRegisterController {
        val registerMap:TreeMap<Any, SubscribeMethod> = TreeMap()
    }

}