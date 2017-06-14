package org.unreal.update.converter

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.lang.reflect.Type


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 16:16
 * 名称：
 */
class FileConverterFactory : Converter.Factory() {
    companion object{
        fun create(): FileConverterFactory {
            return FileConverterFactory()
        }
    }

    override fun responseBodyConverter(type: Type,
                                       annotations : Array<Annotation>,
                                       retrofit : Retrofit)
            : Converter<ResponseBody, File>{
        return FileConverter.INSTANCE
    }


}