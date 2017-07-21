package com.mycreat.kiipu.db

import android.content.Context
import com.mycreat.kiipu.db.bookmark.DaoMaster
import com.mycreat.kiipu.db.bookmark.DaoSession
import com.mycreat.kiipu.db.bookmark.TemplateDao
import com.mycreat.kiipu.db.bookmark.model.Template
import org.greenrobot.greendao.query.WhereCondition

/**
 * Created by zhanghaihai on 2017/7/6.
 */
class KiipuDBUtils {

    companion object{
        private var DB_NAME = "kiipu.db"
        private var daoSession:DaoSession? = null

        public fun getSession(context: Context):DaoSession{
            if(daoSession == null){
                val db = DaoMaster.DevOpenHelper(context, DB_NAME).writableDatabase
                daoSession = DaoMaster(db).newSession()
            }

            return daoSession!!
        }

        fun delTemplate(context:Context, id:Long){
            getSession(context).templateDao.deleteByKey(id)
        }
        fun getTemplate(context:Context, id:Long):List<Template>{
            return getSession(context).templateDao.queryBuilder().where(TemplateDao.Properties.Id.eq(id)).build().list()
        }
        fun getTemplate(context:Context, name:String):List<Template>{
           return getSession(context).templateDao.queryBuilder().where(TemplateDao.Properties.Name.eq(name)).build().list()
        }

        fun saveTemplate(context: Context, template: Template){
            val tmpls = getSession(context).templateDao.queryBuilder().where(TemplateDao.Properties.Name.eq(template.name)).list()
            if(tmpls.size > 0) {
                template.id = tmpls.get(0).id
                getSession(context).templateDao.update(template)
            }
            else {
                getSession(context).templateDao.insert(template)
            }
        }

        fun updateTemplate(context: Context, template: Template){
            getSession(context).templateDao.update(template)
        }
    }


}