package com.mycreat.kiipu.view.bookmark

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.mycreat.kiipu.core.KiipuApplication
import com.mycreat.kiipu.db.KiipuDBUtils
import com.mycreat.kiipu.model.Bookmark
import com.mycreat.kiipu.service.CommonService
import com.mycreat.kiipu.utils.FileUtil
import com.mycreat.kiipu.utils.JsonUtil
import com.mycreat.kiipu.utils.LogUtil
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.MustacheException
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

/**
 * 显示Bookmark详情数据
 * Created by zhanghaihai on 2017/7/7.
 */

class BookmarkTemplateWebVIew : WebView {
    val replacePrefix = "http://tmpl_replace.kiipu.com/"
    val localHtmlPrefix = "http://local_html_prefix.kiipu.com/"
    val defaultTemplate = "common"
    var mBookMark: Bookmark? = null
    var onLinkClickListener: OnLinkClickListener? = null
    var onScrollChangeListener:OnScrollChangedListener? = null
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
            val result = interceptRequest(request?.url.toString())
            return result
        }

        override fun shouldInterceptRequest(view: android.webkit.WebView?, url: String?): WebResourceResponse? {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                return interceptRequest(url)
            else
                return null
        }


    }

    private fun  shouldOverrideUrlLoadingImpl(url: String?): Boolean {
        if(url != null && !url.startsWith(replacePrefix) && url != localHtmlPrefix && mBookMark != null){
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
        if(url == null ) return null;
        if(url.startsWith(replacePrefix) && url.endsWith(".html")) {//发现模板
            var modifiedHtml = loadTemplate(url.removePrefix(replacePrefix))

            if(modifiedHtml != null)
                modifiedHtml = fillData(modifiedHtml)
            if(modifiedHtml != null) {
                return WebResourceResponse("text/html", "utf-8", modifiedHtml.byteInputStream(Charset.forName("utf-8")))
            }else{
                modifiedHtml = msgHtml
                return WebResourceResponse("text/html", "utf-8", modifiedHtml.byteInputStream(Charset.forName("utf-8")))
            }
        }
        return null
    }

    /**
     * 下载模板
     * @param htmlPath  模板地址
     */
    private fun loadTemplate(htmlPath:String?):String?{
        if(htmlPath == null) return null
        val t = KiipuDBUtils.getTemplate(context, mBookMark?.tmplName ?:defaultTemplate, mBookMark?.tmplVersion)
        if(t.isNotEmpty() && File(t[0].local_path).exists()){
            val str = FileUtil.readUtfFromFile(t[0].local_path)
            if(!str.isNullOrEmpty()){
                return str
            }
        }
        val call = KiipuApplication.mRetrofitTemplateService.requestHtml(htmlPath)
        return try {
            val rep = call.execute()
            if(rep.isSuccessful) {
                val str = rep.body().toString()
                if(mBookMark != null) {
                    CommonService.TemplateCacheEvent(str, htmlPath, mBookMark!!.tmplName ?:defaultTemplate , mBookMark!!.tmplVersion).post()
                }
                str
            }else{
                null
            }
        }catch (e:IOException){
            null
        }
    }

    /**
     * 利用mustache填充数据到模板
     */
    private fun fillData(template:String):String{
        var resultHtml = ""
        if(mBookMark != null) {
            try {
                val json = JsonUtil.toJson(mBookMark)
                val map = JsonUtil.getMapTree(json, "");
                val resultMap = HashMap<String, Any>()
                if(map != null) {
                    map.forEach {
                        if(it.value is Map<*, *>){ //发现map对象提取到高层，若key重复将不替换
                            val internalMap = it.value as Map<*, *>
                            internalMap.forEach { entity ->
                                if(!resultMap.containsKey(entity.key) && entity.value is Any){
                                    resultMap.put(entity.key.toString(), entity.value as Any)
                                }
                            }
                        }else{
                            resultMap.put(it.key, it.value)
                        }
                    }
                    resultHtml = Mustache.compiler().compile(template).execute(resultMap)
                }
            }catch (e: MustacheException){
                e.printStackTrace()
            }

        }
        LogUtil.d(resultHtml)
        return resultHtml

    }

    fun refresh(bookmark:Bookmark){
        mBookMark = bookmark
        loadData(emptyMsgHtml, "text/html", "utf-8")
        if(bookmark.tmplName != null) {
            loadUrl("$replacePrefix${bookmark.tmplName}/${mBookMark!!.tmplVersion}.html")
        }else{
            loadUrl("${replacePrefix}$defaultTemplate/1.html" )
        }
    }

    interface OnLinkClickListener{
        fun onClick(url:String, bookmark:Bookmark)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangeListener?.onScrollChanged(l - oldl, t - oldt) //垂直滚动式屏蔽 RecyclerView 滚动
    }

    public interface OnScrollChangedListener{
        fun onScrollChanged( dx: Int, dy : Int)
    }
    var dx = 0.0f
    var dy = 0.0f
    var lx = 0.0f
    var ly = 0.0f
    var shouldVerticalScroll = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                shouldVerticalScroll = false
                onScrollChangeListener?.onScrollChanged(0, 0) //恢复 RecyclerView 滚动状态
                dx = 0f
                dy = 0f
                lx = event.x
                ly = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if(!shouldVerticalScroll ) {
                    dx = event.x - lx
                    dy = event.y - ly
                    shouldVerticalScroll = Math.abs(dy) > Math.abs(dx)
                    if(shouldVerticalScroll) {
                        onScrollChangeListener?.onScrollChanged(dx.toInt(), dy.toInt())
                        return true;
                    }
                }
            }
            MotionEvent.ACTION_UP -> {

            }

        }
        return super.onTouchEvent(event)
    }

    private val msgHtml = "<!doctype html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\">\n" +
            "  <title>{{title}}</title>\n" +
            "  <meta name=\"viewport\" content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no,initial-scale=1,minimal-ui\">\n" +
            "  <style>\n" +
            "    html{line-height:1.15;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%;}body{margin:0;-webkit-tap-highlight-color: rgba(0,0,0,0);}article,aside,footer,header,nav,section{display:block;}h1{font-size:2em;margin:0.67em 0;}figcaption,figure,main{display:block;}figure{margin:1em 40px;}hr{box-sizing:content-box;height:0;overflow:visible;}pre{font-family:monospace,monospace;font-size:1em;}a{background-color:transparent;-webkit-text-decoration-skip:objects;}abbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted;}b,strong{font-weight:inherit;}b,strong{font-weight:bolder;}code,kbd,samp{font-family:monospace,monospace;font-size:1em;}dfn{font-style:italic;}mark{background-color:#ff0;color:#000;}small{font-size:80%;}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline;}sub{bottom:-0.25em;}sup{top:-0.5em;}audio,video{display:inline-block;}audio:not([controls]){display:none;height:0;}img{border-style:none;}svg:not(:root){overflow:hidden;}button,input,optgroup,select,textarea{font-family:sans-serif;font-size:100%;line-height:1.15;margin:0;}button,input{overflow:visible;}button,select{text-transform:none;}button,html [type=\"button\"],[type=\"reset\"],[type=\"submit\"]{-webkit-appearance:button;}button::-moz-focus-inner,[type=\"button\"]::-moz-focus-inner,[type=\"reset\"]::-moz-focus-inner,[type=\"submit\"]::-moz-focus-inner{border-style:none;padding:0;}button:-moz-focusring,[type=\"button\"]:-moz-focusring,[type=\"reset\"]:-moz-focusring,[type=\"submit\"]:-moz-focusring{outline:1px dotted ButtonText;}fieldset{padding:0.35em 0.75em 0.625em;}legend{box-sizing:border-box;color:inherit;display:table;max-width:100%;padding:0;white-space:normal;}progress{display:inline-block;vertical-align:baseline;}textarea{overflow:auto;}[type=\"checkbox\"],[type=\"radio\"]{box-sizing:border-box;padding:0;}[type=\"number\"]::-webkit-inner-spin-button,[type=\"number\"]::-webkit-outer-spin-button{height:auto;}[type=\"search\"]{-webkit-appearance:textfield;outline-offset:-2px;}[type=\"search\"]::-webkit-search-cancel-button,[type=\"search\"]::-webkit-search-decoration{-webkit-appearance:none;}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit;}details,menu{display:block;}summary{display:list-item;}canvas{display:inline-block;}template{display:none;}[hidden]{display:none;}\n" +
            "  </style>\n" +
            "  <style>\n" +
            "    ul{margin:0;padding:0}li{list-style:none}a{text-decoration:none}h1,p{margin:0;padding:0}.wrap{padding:16px 20px;color:#262626}.tags{display:flex;overflow-y:scroll}.tags .tag{display:inline-block;height:30px;padding:0 12px;font-size:14px;line-height:30px;vertical-align:top;background:#eef4fa;border-radius:100px;margin:3px 5px 3px 0;white-space:nowrap}.tags .tag a{color:#3e7ac2}.title{font-size:18px;margin-top:14px;margin-bottom:8px}.content{font-size:15px;line-height:25px}.actions{display:flex;margin-top:12px}.actions .btn{color:#0f88eb;border:1px solid #0f88eb;border-radius:3px;cursor:pointer;background:0;font-size:14px;text-align:center;line-height:30px;padding:0 12px}.actions .btn:hover{background-color:rgba(13,121,209,.06)}.actions .btn:active{background-color:rgba(13,121,209,.06)}\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"wrap\">\n" +
            "    <h1 class=\"title\">模板填充数据失败！</h1>\n" +
            "  </div>\n" +
            "</body>\n" +
            "</html>\n"

    //用于清空之前的webview内容
    private val emptyMsgHtml = "about：blank"
}
