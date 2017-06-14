package org.unreal.update.http.helper.module

import me.jessyan.progressmanager.ProgressManager
import org.unreal.core.configure.DOWNLOAD_FILE_URL
import org.unreal.update.converter.FileConverterFactory
import org.unreal.update.http.helper.HttpClientHelper
import org.unreal.update.http.helper.service.UpdateService
import org.unreal.update.progress.ProgressResponseListener
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:38
 * 名称：
 */
class ServiceMoudle {
    companion object{
        private val builder = Retrofit.Builder()
                .baseUrl(DOWNLOAD_FILE_URL)
                .addConverterFactory(FileConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //添加FileConverterFactory
        /**
         * 创建带响应进度(下载进度)回调的service
         */
        fun <T> createResponseService(tClass: Class<T>, listener: ProgressResponseListener): T {
            return builder
                    .client(HttpClientHelper.addProgressResponseListener(listener))
                    .build()
                    .create(tClass)
        }
        /**
         * 创建带响应进度(下载进度)回调的service
         */
        fun <T> createResponseService(tClass: Class<T>): T {
            return builder
                    .client(HttpClientHelper.addProgressListener())
                    .build()
                    .create(tClass)
        }

    }
}