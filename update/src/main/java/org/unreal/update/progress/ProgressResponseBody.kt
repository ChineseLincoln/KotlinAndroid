package org.unreal.update.progress

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*



/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:03
 * 名称：
 */
class ProgressResponseBody (val responseBody: ResponseBody,
                            val progressListener: ProgressResponseListener) : ResponseBody() {

    var bufferedSource: BufferedSource? = null

    /**
     * 文件保存路径
     */
    var savePath : String = ""

    /**
     * 下载文件名
     */
    var fileName: String = ""


    /**
     * 重写调用实际的响应体的contentType
     */
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    /**
     * 重写调用实际的响应体的contentLength
     */
    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    /**
     * 重写进行包装source
     */
    override fun source(): BufferedSource? {
        if(bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource
    }

    /**
     * 读取，回调进度接口
     */
    private fun source(source: Source): ForwardingSource {
        return object : ForwardingSource(source) {
            //当前读取字节数
            internal var totalBytesRead = 0L

            @Override
            override fun read(sink: Buffer?, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                //回调，如果contentLength()不知道长度，会返回-1
                Observable.just(progressListener)
                        .filter({ progressResponseListener -> progressResponseListener != null })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ progressListener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1L) })
                return bytesRead
            }
        }
    }
}
