package com.mycreat.kiipu.rxbus

/**
 * 用于指定订阅者所在的线程
 * Created by zhanghaihai on 2017/7/11.
 */
enum class ThreadMode {
    MAIN,          // 主线程, UI线程
    IO,            // IO 线程, IO 操作线程，包括文件操作，数据库操作，网络操作
    NEW_THREAD,    // 在新的线程, 每次都会创建新的线程
    COMPUTATION,   // 用于计算的线程, CPU 密集型任务
    IMMEDIATE      // 在当前线程
}