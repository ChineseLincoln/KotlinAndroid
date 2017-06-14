package org.unreal.update.converter

import okhttp3.ResponseBody
import org.unreal.update.manger.DownlaodManger
import retrofit2.Converter
import java.io.File


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 16:22
 * 名称：
 */
class FileConverter : Converter<ResponseBody, File> {
    companion object{
        val INSTANCE = FileConverter()
    }
    override fun convert(value: ResponseBody): File? {
        return FileUtils.writeResponseBodyToDisk(value, DownlaodManger.downloadFileSavePath)
    }
}