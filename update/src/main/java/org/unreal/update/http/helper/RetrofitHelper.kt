package org.unreal.update.http.helper

import me.jessyan.progressmanager.ProgressManager
import okhttp3.OkHttpClient
import org.unreal.update.converter.FileConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:38
 * 名称：
 */
class RetrofitHelper {
    companion object{
        private val builder = Retrofit.Builder()
                .baseUrl("http://www.google.com")
                .addConverterFactory(FileConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //添加FileConverterFactory
        /**
         * 创建带响应进度(下载进度)回调的service
         */
        fun <T> createResponseService(tClass: Class<T>): T {
            return builder
                    .client(addProgressListener())
                    .build()
                    .create(tClass)
        }

        fun addProgressListener() : OkHttpClient =
                ProgressManager.getInstance()
                        .with(OkHttpClient.Builder())
                        .build()

    }

}