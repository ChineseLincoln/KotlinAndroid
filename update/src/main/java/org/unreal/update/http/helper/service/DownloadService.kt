package org.unreal.update.http.helper.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:49
 * 名称：
 */
interface DownloadService {
    @Streaming
    @GET
    fun downloadWithDynamicUrl(@Url downloadUrl: String): Observable<File>
}