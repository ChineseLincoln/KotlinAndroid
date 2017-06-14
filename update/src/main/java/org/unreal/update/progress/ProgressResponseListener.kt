package org.unreal.update.progress

/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:01
 * 名称：
 */
interface ProgressResponseListener{
    fun onResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean)
}