package com.mycreat.kiipu.view

import android.content.Context
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.mycreat.kiipu.core.KiipuApplication
import com.mycreat.kiipu.model.Bookmark
import com.mycreat.kiipu.model.BookmarkExt
import com.samskivert.mustache.Mustache
import java.io.IOException
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 显示Bookmark详情数据
 * Created by zhanghaihai on 2017/7/7.
 */

class BookmarkWebVIew : WebView {
    var bookMark: Bookmark? = null
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

        @RequiresApi(android.os.Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?): WebResourceResponse? {
            return interceptRequest(request?.url.toString(), BookmarkExt())
        }

        override fun shouldInterceptRequest(view: android.webkit.WebView?, url: String?): WebResourceResponse? {
            return interceptRequest(url, BookmarkExt())
        }


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
    fun interceptRequest(url:String?, data: BookmarkExt):WebResourceResponse?{
        var modifiedHtml = loadTemplate(url)
        if(modifiedHtml != null ) {
            modifiedHtml = fillData(modifiedHtml, BookmarkExt())
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
    fun fillData(template:String, data: BookmarkExt):String{
        return Mustache.compiler().withFormatter(object : Mustache.Formatter {
            val _fmt: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            override fun format(value: Any): String {
                if (value is Date)
                    return _fmt.format(value)
                else
                    return value.toString()
            }

        }).compile(template).execute(object : Any() {
            val topics = bookMark?.ext?.topics
            val title = bookMark?.ext?.title
            val note = bookMark?.ext?.note
        })

    }


    fun setBookmark( bookMark: Bookmark){
        this.bookMark = bookMark
        refresh()
    }

    fun refresh(){
        if(bookMark != null)
            loadUrl(bookMark!!.tmplName)
    }

}
