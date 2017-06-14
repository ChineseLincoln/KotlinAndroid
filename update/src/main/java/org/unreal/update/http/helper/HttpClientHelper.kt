package org.unreal.update.http.helper

import me.jessyan.progressmanager.ProgressManager
import okhttp3.OkHttpClient


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:24
 * 名称：
 */
object HttpClientHelper {
    /**
     * 包装OkHttpClient，用于下载文件的回调
     */
    fun addProgressListener() : OkHttpClient = ProgressManager.getInstance().with(OkHttpClient.Builder())
            .build()
}