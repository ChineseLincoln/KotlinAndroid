package org.unreal.web

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web.*
import org.unreal.web.api.JsApi


class WebActivity : AppCompatActivity() {

    private var titleStr = ""
    private var url = ""
    private var hasTitleBar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        getIntentData()
        initWebView()
        initJsBridge()
        web.loadUrl(url)
        toolbar.setNavigationOnClickListener{
            if(web.canGoBack()){
                web.goBack()
            }else{
                finish()
            }
        }
        close.setOnClickListener { finish() }
    }

    private fun initJsBridge(){
        web.setJavascriptInterface(JsApi(this))
    }

    private fun initWebView() {
        if(!hasTitleBar){
            toolbar.visibility = View.GONE
        }
        web.setWebChromeClient(object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                if (this@WebActivity.title == "") {
                    setTitle(title)
                }else{
                    toolbarTitle.text = titleStr
                }
                super.onReceivedTitle(view, title)
            }

            override fun onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt()
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                callback.invoke(origin, true, false)//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
            }

        })

        web.setWebViewClient(WebViewClientWrapper(this))

        web.settings.setGeolocationEnabled(true)
        // 设置可以支持缩放
        web.settings.setSupportZoom(true)
        // 设置出现缩放工具
        web.settings.builtInZoomControls = true
        //扩大比例的缩放
        web.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        web.settings.loadWithOverviewMode = true
    }

    private fun getIntentData() {
        titleStr = intent.getStringExtra(TITLE)
        url = intent.getStringExtra(URL)
        hasTitleBar = intent.getBooleanExtra(HAS_TITLE_BAR, true)
    }

    companion object{
        const val TITLE = "title"
        const val URL = "url"
        const val HAS_TITLE_BAR = "titleBar"
    }

    fun hideProgress() {
        progress.visibility = GONE
    }

    override fun onBackPressed() {
        if(web.canGoBack()){
            web.goBack()
        }else{
            finish()
        }
    }

}

class WebViewClientWrapper (val activity: WebActivity): WebViewClient() {

    /*@Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
    }*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return processUrl(request?.url.toString())
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return processUrl(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        activity.hideProgress()
    }

    private fun processUrl(url: String): Boolean {
        when {
            (url.contains("tel:")
                    || url.contains("sip:")
                    || url.contains("sms:")
                    || url.contains("smsto:")
                    || url.contains("mailto:")) -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(intent)
                return true
            }
            url == "http://www.drawthink.com/exit" -> {
                activity.finish()
                return true
            }
            else -> return false
        }
    }
}
