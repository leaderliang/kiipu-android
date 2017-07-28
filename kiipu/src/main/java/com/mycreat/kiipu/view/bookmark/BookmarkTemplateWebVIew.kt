package com.mycreat.kiipu.view.bookmark

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.mycreat.kiipu.core.KiipuApplication
import com.mycreat.kiipu.model.Bookmark
import com.mycreat.kiipu.model.BookmarkExt
import com.mycreat.kiipu.utils.LogUtil
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.MustacheException
import java.io.IOException
import java.nio.charset.Charset

/**
 * 显示Bookmark详情数据
 * Created by zhanghaihai on 2017/7/7.
 */

class BookmarkTemplateWebVIew : WebView {
    val replacePrefix = "http://tmpl_replace.kiipu.com/"
    var mBookMark: Bookmark? = null
    var onLinkClickListener: OnLinkClickListener? = null
    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private fun initialize() {
        setWebChromeClient(mWebViewChromeClient)
        setWebViewClient(mWebViewClient)
    }

    private val mWebViewClient = object : android.webkit.WebViewClient(){
        override fun onPageStarted(view: android.webkit.WebView?, url: String?, favicon: android.graphics.Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                return shouldOverrideUrlLoadingImpl(request?.url.toString())
            }

            return false
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return shouldOverrideUrlLoadingImpl(url)
        }

        @RequiresApi(android.os.Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?): WebResourceResponse? {
            return interceptRequest(request?.url.toString())
        }

        override fun shouldInterceptRequest(view: android.webkit.WebView?, url: String?): WebResourceResponse? {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                return interceptRequest(url)
            else
                return null
        }


    }

    private fun  shouldOverrideUrlLoadingImpl(url: String?): Boolean {
        if(url != null && !url.startsWith(replacePrefix) && mBookMark != null){
            onLinkClickListener?.onClick(url, mBookMark!!)
            return true
        }

        return false
    }

    private val mWebViewChromeClient = object : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: android.webkit.WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            //LogUtil.d("Progress :" + newProgress)
        }

    }

    /**
     * 拦截请求, 做出相应的操作
     * @param url 请求地址
     * @param data 用于填充模板的数据
     */
    fun interceptRequest(url:String?):WebResourceResponse?{

        var modifiedHtml = loadTemplate(url?.removePrefix(replacePrefix))
        if(modifiedHtml != null ) {
            modifiedHtml = fillData(modifiedHtml)
            return WebResourceResponse("text/html", "utf-8", modifiedHtml.byteInputStream(Charset.forName("utf-8")))
        }else{
            return null
        }
    }

    /**
     * 下载模板
     * @param htmlPath  模板地址
     */
    fun loadTemplate(htmlPath:String?):String?{
        if(htmlPath == null) return null
        val call = KiipuApplication.mRetrofitTemplateService.requestHtml(htmlPath)
        try {
            val rep = call.execute()
            return if(rep.isSuccessful) rep.body().toString() else null
        }catch (e:IOException){
            return null
        }
    }

    /**
     * 利用mustache填充数据到模板
     */
    fun fillData(template:String):String{
        var resultHtml = ""
        if(mBookMark != null && mBookMark!!.ext != null) {
            try {
                resultHtml = Mustache.compiler().compile(template).execute(object : Any() {
                    val topics = if(mBookMark!!.ext?.topics == null) ArrayList<BookmarkExt.Topic>() else mBookMark!!.ext!!.topics
                    val userlink = if(mBookMark?.ext?.userLink == null) "" else mBookMark!!.ext.userLink
                    val username= if(mBookMark?.ext?.userName == null) "" else mBookMark!!.ext.userName
                    val avatar = if(mBookMark?.ext?.avatar == null) "" else mBookMark!!.ext.avatar

                    val title = if( mBookMark!!.info?.title != null)  "" else mBookMark!!.info.title
                    val note = if(mBookMark!!.info!!.introduce == null) "" else mBookMark!!.info!!.introduce
                    val url = if(mBookMark?.info?.url == null) "" else mBookMark!!.info!!.url
                    val img = if(mBookMark?.info?.img == null) "" else mBookMark!!.info!!.img
                })
            }catch (e: MustacheException){
                e.printStackTrace()
            }

        }
        LogUtil.d(resultHtml)
        return resultHtml

    }

    fun refresh(bookmark:Bookmark){
        mBookMark = bookmark
        if(bookmark.tmplName != null) {
            loadUrl(replacePrefix + bookmark.tmplName + "/" + mBookMark!!.tmplVersion + ".html")
        }
    }

    interface OnLinkClickListener{
        fun onClick(url:String, bookmark:Bookmark)
    }

}
