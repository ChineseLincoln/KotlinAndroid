package org.unreal.update.http.helper

import org.unreal.update.progress.ProgressResponseBody
import okhttp3.OkHttpClient
import org.unreal.update.converter.FileConverter
import org.unreal.update.progress.ProgressResponseListener
import me.jessyan.progressmanager.ProgressManager




/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:24
 * 名称：
 */
object HttpClientHelper {
    /**
     * 包装OkHttpClient，用于下载文件的回调
     */
    fun addProgressResponseListener(progressListener: ProgressResponseListener): OkHttpClient {
        val client = OkHttpClient.Builder()
        //增加拦截器
        client.addInterceptor { chain ->
            //拦截
            val originalResponse = chain.proceed(chain.request())
            val body = originalResponse.body()?.let { ProgressResponseBody(it, progressListener) }
            if (body != null) {
                body.savePath = chain.request().header(FileConverter.SAVE_PATH)
                val segments = chain.request().url().pathSegments()
                body.fileName = segments[segments.size - 1]
            }
            //包装响应体并返回
            originalResponse.newBuilder()
                    .body(body)
                    .build()
        }
        return client.build()
    }
    /**
     * jessyan包装的http
     */
    fun addProgressListener() : OkHttpClient = ProgressManager.getInstance().with(OkHttpClient.Builder())
            .build()
}