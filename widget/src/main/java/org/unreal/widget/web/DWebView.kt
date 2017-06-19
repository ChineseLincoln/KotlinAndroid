package org.unreal.widget.web

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.Keep
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.FrameLayout
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException
import java.lang.reflect.Method
import java.net.URLEncoder
import java.util.*


/**
 * Created by du on 16/12/29.
 */

class DWebView : WebView {
    private var jsb: Any? = null
    private var APP_CACAHE_DIRNAME: String? = null
    internal var callID = 0
    internal var handlerMap: MutableMap<Int, OnReturnValue> = HashMap()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initWebView()
    }

    constructor(context: Context) : super(context) {
        initWebView()
    }

    @Keep
    internal fun initWebView() {
        APP_CACAHE_DIRNAME = context.filesDir.absolutePath + "/webcache"
        val settings = settings
        settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        }
        settings.allowFileAccess = false
        settings.setAppCacheEnabled(false)
        settings.savePassword = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.setSupportMultipleWindows(true)
        settings.setAppCachePath(APP_CACAHE_DIRNAME)
        if (Build.VERSION.SDK_INT >= 21) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        settings.useWideViewPort = true
        super.setWebChromeClient(mWebChromeClient)
        super.addJavascriptInterface(object : Any() {
            @Keep
            @JavascriptInterface
            fun call(methodName: String, args: String): String {
                var error = "Js bridge method called, but there is " + "not a JavascriptInterface object, please set JavascriptInterface object first!"
                if (jsb == null) {
                    Log.e("SynWebView", error)
                    return ""
                }

                val cls = jsb!!.javaClass
                try {
                    var method: Method?
                    var asyn = false
                    val arg = JSONObject(args)
                    var callback = ""
                    try {
                        callback = arg.getString("_dscbstub")
                        arg.remove("_dscbstub")
                        method = cls.getDeclaredMethod(methodName,
                                *arrayOf(JSONObject::class.java, CompletionHandler::class.java))
                        asyn = true
                    } catch (e: Exception) {
                        method = cls.getDeclaredMethod(methodName, *arrayOf<Class<*>>(JSONObject::class.java))
                    }

                    if (method == null) {
                        error = "ERROR! \n Not find method \"$methodName\" implementation! "
                        Log.e("SynWebView", error)
                        evaluateJavascript(String.format("alert(decodeURIComponent(\"%s\"})", error))
                        return ""
                    }
                    var annotation: JavascriptInterface? = null
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        annotation = method.getAnnotation(JavascriptInterface::class.java)
                    }
                    if (annotation != null) {
                        var ret: Any?
                        method.isAccessible = true
                        if (asyn) {
                            val cb = callback
                            ret = method.invoke(jsb, arg, object : CompletionHandler {

                                override fun complete(retValue: String) {
                                    complete(retValue, true)
                                }

                                override fun complete() {
                                    complete("", true)
                                }

                                override fun setProgressData(value: String) {
                                    complete(value, false)
                                }

                                private fun complete(retValue: String?, complete: Boolean) {
                                    var retValue = retValue
                                    try {
                                        if (retValue == null) retValue = ""
                                        retValue = URLEncoder.encode(retValue, "UTF-8").replace("\\+".toRegex(), "%20")
                                        var script = String.format("%s(decodeURIComponent(\"%s\"));", cb, retValue)
                                        if (complete) {
                                            script += "delete window." + cb
                                        }
                                        evaluateJavascript(script)
                                    } catch (e: UnsupportedEncodingException) {
                                        e.printStackTrace()
                                    }

                                }
                            })
                        } else {
                            ret = method.invoke(jsb, arg)
                        }
                        if (ret == null) {
                            ret = ""
                        }
                        return ret.toString()
                    } else {
                        error = "Method " + methodName + " is not invoked, since  " +
                                "it is not declared with JavascriptInterface annotation! "
                        evaluateJavascript(String.format("alert('ERROR \\n%s')", error))
                        Log.e("SynWebView", error)
                    }
                } catch (e: Exception) {
                    evaluateJavascript(String.format("alert('ERROR! \\n调用失败：函数名或参数错误 ［%s］')", e.message))
                    e.printStackTrace()
                }

                return ""
            }

            internal var i = 0

            @Keep
            @JavascriptInterface
            fun returnValue(id: Int, value: String) {
                val handler = handlerMap[id]
                if (handler != null) {
                    handler.onValue(value)
                    handlerMap.remove(id)
                }
            }

        }, BRIDGE_NAME)

    }

    override fun setWebChromeClient(client: WebChromeClient) {
        webChromeClient = client
    }

    internal var webChromeClient: WebChromeClient? = null

    private val mWebChromeClient = object : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            injectJs()
            if (webChromeClient != null) {
                webChromeClient!!.onProgressChanged(view, newProgress)
            } else {
                super.onProgressChanged(view, newProgress)
            }
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            injectJs()
            if (webChromeClient != null) {
                webChromeClient!!.onReceivedTitle(view, title)
            } else {
                super.onReceivedTitle(view, title)
            }
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            if (webChromeClient != null) {
                if (webChromeClient!!.onJsAlert(view, url, message, result)) {
                    return true
                }
            }
            result.confirm()
            post {
                val alertDialog = AlertDialog.Builder(context).setTitle("提示").setMessage(message).setPositiveButton("确定") { dialog, which -> dialog.dismiss() }
                        .create()
                alertDialog.show()
            }
            return true
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap) {
            if (webChromeClient != null) {
                webChromeClient!!.onReceivedIcon(view, icon)
            } else {
                super.onReceivedIcon(view, icon)
            }
        }

        override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
            if (webChromeClient != null) {
                webChromeClient!!.onReceivedTouchIconUrl(view, url, precomposed)
            } else {
                super.onReceivedTouchIconUrl(view, url, precomposed)
            }
        }

        override fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
            if (webChromeClient != null) {
                webChromeClient!!.onShowCustomView(view, callback)
            } else {
                super.onShowCustomView(view, callback)
            }
        }

        override fun onShowCustomView(view: View, requestedOrientation: Int,
                                      callback: WebChromeClient.CustomViewCallback) {
            if (webChromeClient != null) {
                webChromeClient!!.onShowCustomView(view, requestedOrientation, callback)
            } else {
                super.onShowCustomView(view, requestedOrientation, callback)
            }
        }

        override fun onHideCustomView() {
            if (webChromeClient != null) {
                webChromeClient!!.onHideCustomView()
            } else {
                super.onHideCustomView()
            }
        }

        override fun onCreateWindow(view: WebView, isDialog: Boolean,
                                    isUserGesture: Boolean, resultMsg: Message): Boolean {
            if (webChromeClient != null) {
                return webChromeClient!!.onCreateWindow(view, isDialog,
                        isUserGesture, resultMsg)
            }
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }

        override fun onRequestFocus(view: WebView) {
            if (webChromeClient != null) {
                webChromeClient!!.onRequestFocus(view)
            } else {
                super.onRequestFocus(view)
            }
        }

        override fun onCloseWindow(window: WebView) {
            if (webChromeClient != null) {
                webChromeClient!!.onCloseWindow(window)
            } else {
                super.onCloseWindow(window)
            }
        }


        override fun onJsConfirm(view: WebView, url: String, message: String,
                                 result: JsResult): Boolean {
            if (webChromeClient != null && webChromeClient!!.onJsConfirm(view, url, message, result)) {
                return true
            } else {
                val mHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        throw RuntimeException()
                    }
                }
                val listener = DialogInterface.OnClickListener { dialog, which ->
                    if (which == Dialog.BUTTON_POSITIVE) {
                        result.confirm()
                    } else {
                        result.cancel()
                    }
                    mHandler.sendEmptyMessage(1)
                }
                AlertDialog.Builder(context).setTitle("提示")
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("确定", listener)
                        .setNegativeButton("取消", listener).show()

                try {
                    Looper.loop()
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }

                return true

            }

        }

        override fun onJsPrompt(view: WebView, url: String, message: String,
                                defaultValue: String, result: JsPromptResult): Boolean {
            if (webChromeClient != null && webChromeClient!!.onJsPrompt(view, url, message, defaultValue, result)) {
                return true
            } else {
                val mHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        throw RuntimeException()
                    }
                }
                val editText = EditText(context)
                editText.setText(defaultValue)
                val dpi = context.resources.displayMetrics.density
                val listener = DialogInterface.OnClickListener { dialog, which ->
                    if (which == Dialog.BUTTON_POSITIVE) {
                        result.confirm(editText.text.toString())
                    } else {
                        result.cancel()
                    }
                    mHandler.sendEmptyMessage(1)
                }
                AlertDialog.Builder(context)
                        .setTitle(message)
                        .setView(editText)
                        .setCancelable(false)
                        .setPositiveButton("确定", listener)
                        .setNegativeButton("取消", listener)
                        .show()
                val layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                val t = (dpi * 16).toInt()
                layoutParams.setMargins(t, 0, t, 0)
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                editText.layoutParams = layoutParams
                val padding = (15 * dpi).toInt()
                editText.setPadding(padding - (5 * dpi).toInt(), padding, padding, padding)
                try {
                    Looper.loop()
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }

                return true
            }

        }

        override fun onJsBeforeUnload(view: WebView, url: String, message: String, result: JsResult): Boolean {
            if (webChromeClient != null) {
                return webChromeClient!!.onJsBeforeUnload(view, url, message, result)
            }
            return super.onJsBeforeUnload(view, url, message, result)
        }

        override fun onExceededDatabaseQuota(url: String, databaseIdentifier: String, quota: Long,
                                             estimatedDatabaseSize: Long,
                                             totalQuota: Long,
                                             quotaUpdater: WebStorage.QuotaUpdater) {
            if (webChromeClient != null) {
                webChromeClient!!.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater)
            } else {
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater)
            }
        }

        override fun onReachedMaxAppCacheSize(requiredStorage: Long, quota: Long, quotaUpdater: WebStorage.QuotaUpdater) {
            if (webChromeClient != null) {
                webChromeClient!!.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
            }
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
            if (webChromeClient != null) {
                webChromeClient!!.onGeolocationPermissionsShowPrompt(origin, callback)
            } else {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }
        }

        override fun onGeolocationPermissionsHidePrompt() {
            if (webChromeClient != null) {
                webChromeClient!!.onGeolocationPermissionsHidePrompt()
            } else {
                super.onGeolocationPermissionsHidePrompt()
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequest(request: PermissionRequest) {
            if (webChromeClient != null) {
                webChromeClient!!.onPermissionRequest(request)
            } else {
                super.onPermissionRequest(request)
            }
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequestCanceled(request: PermissionRequest) {
            if (webChromeClient != null) {
                webChromeClient!!.onPermissionRequestCanceled(request)
            } else {
                super.onPermissionRequestCanceled(request)
            }
        }

        override fun onJsTimeout(): Boolean {
            if (webChromeClient != null) {
                return webChromeClient!!.onJsTimeout()
            }
            return super.onJsTimeout()
        }

        override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
            if (webChromeClient != null) {
                webChromeClient!!.onConsoleMessage(message, lineNumber, sourceID)
            } else {
                super.onConsoleMessage(message, lineNumber, sourceID)
            }
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            if (webChromeClient != null) {
                return webChromeClient!!.onConsoleMessage(consoleMessage)
            }
            return super.onConsoleMessage(consoleMessage)
        }

        override fun getDefaultVideoPoster(): Bitmap {

            if (webChromeClient != null) {
                return webChromeClient!!.defaultVideoPoster
            }
            return super.getDefaultVideoPoster()
        }

        override fun getVideoLoadingProgressView(): View {
            if (webChromeClient != null) {
                return webChromeClient!!.videoLoadingProgressView
            }
            return super.getVideoLoadingProgressView()
        }

        override fun getVisitedHistory(callback: ValueCallback<Array<String>>) {
            if (webChromeClient != null) {
                webChromeClient!!.getVisitedHistory(callback)
            } else {
                super.getVisitedHistory(callback)
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                                       fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            if (webChromeClient != null) {
                return webChromeClient!!.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }

        private fun injectJs() {
            evaluateJavascript("function getJsBridge(){window._dsf=window._dsf||{};return{call:function(b,a,c){\"function\"==typeof a&&(c=a,a={});if(\"function\"==typeof c){window.dscb=window.dscb||0;var d=\"dscb\"+window.dscb++;window[d]=c;a._dscbstub=d}a=JSON.stringify(a||{});return window._dswk?prompt(window._dswk+b,a):\"function\"==typeof _dsbridge?_dsbridge(b,a):_dsbridge.call(b,a)},register:function(b,a){\"object\"==typeof b?Object.assign(window._dsf,b):window._dsf[b]=a}}}dsBridge=getJsBridge();")
        }
    }

    private fun _evaluateJavascript(script: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(script, null)
        } else {
            loadUrl("javascript:" + script)
        }
    }

    //如果当前在主线程，不要直接调用post,这可能会延迟js执行
    fun evaluateJavascript(script: String) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            _evaluateJavascript(script)
        } else {
            post { _evaluateJavascript(script) }
        }
    }

    override fun clearCache(includeDiskFiles: Boolean) {
        super.clearCache(includeDiskFiles)
        CookieManager.getInstance().removeAllCookie()
        val context = context
        //清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db")
            context.deleteDatabase("webviewCache.db")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //WebView 缓存文件
        val appCacheDir = File(APP_CACAHE_DIRNAME!!)
        val webviewCacheDir = File(context.cacheDir
                .absolutePath + "/webviewCache")

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir)
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir)
        }
    }

    fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                val files = file.listFiles()
                for (i in files.indices) {
                    deleteFile(files[i])
                }
            }
            file.delete()
        } else {
            Log.e("Webview", "delete file no exists " + file.absolutePath)
        }
    }

    override fun loadUrl(url: String) {
        post { super@DWebView.loadUrl(url) }
    }

    @JvmOverloads fun callHandler(method: String, args: Array<Any>?, handler: OnReturnValue? = null) {
        var args = args
        if (args == null) args = arrayOf<Any>(0)
        val arg = JSONArray(Arrays.asList(*args)).toString()
        var script = String.format("(window._dsf.%s||window.%s).apply(window._dsf||window,%s)", method, method, arg)
        if (handler != null) {
            script = String.format("%s.returnValue(%d,%s)", BRIDGE_NAME, callID, script)
            handlerMap.put(callID++, handler)
        }
        evaluateJavascript(script)

    }

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        post { super@DWebView.loadUrl(url, additionalHttpHeaders) }
    }

    fun setJavascriptInterface(`object`: Any) {
        jsb = `object`
    }

    companion object {
        private val BRIDGE_NAME = "_dsbridge"
    }
}