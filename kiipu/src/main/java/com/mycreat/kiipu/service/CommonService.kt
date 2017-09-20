package com.mycreat.kiipu.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mycreat.kiipu.core.KiipuApplication
import com.mycreat.kiipu.db.KiipuDBUtils
import com.mycreat.kiipu.db.bookmark.model.Template
import com.mycreat.kiipu.rxbus.RxBus
import com.mycreat.kiipu.rxbus.RxBusSubscribe
import com.mycreat.kiipu.rxbus.ThreadMode
import com.mycreat.kiipu.utils.FileUtil
import java.io.File
import java.io.IOException

/**
 *
 * Created by zhanghaihai on 2017/7/11.
 */
class CommonService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        RxBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY //防止被杀死
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getDefault().unregister(this)
    }

    /**
     * 存储模板数据到文件及数据库
     */
    @RxBusSubscribe(ThreadMode.IO)
    fun onEventStorage(event:TemplateCacheEvent){
        val tFolder = FileUtil.getTemplateCacheDir()
        val tPath = tFolder + File.separator + event.name + "_" + event.versionCode
        try {
            if(!File(tFolder).exists()) File(tFolder).mkdirs()
            FileUtil.writeToFile( event.template, tPath)
            val template = Template()
            template.id = 0
            template.name = event.name
            template.url = event.url
            template.version_code = event.versionCode
            template.local_path = tPath
            KiipuDBUtils.saveTemplate(KiipuApplication.appContext, template);
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    class TemplateCacheEvent(var template:String, var url:String, var name:String, var versionCode:Int = 1){
        fun post(){
            RxBus.getDefault().post(this)
        }
    }
}