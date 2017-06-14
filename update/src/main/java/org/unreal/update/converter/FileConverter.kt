package org.unreal.update.converter

import android.os.Environment.getExternalStorageDirectory
import android.text.TextUtils
import me.jessyan.progressmanager.body.ProgressResponseBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.File


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 16:22
 * 名称：
 */
class FileConverter : Converter<ResponseBody, File> {
    companion object{
        /**
         * 添加请求头的key,后面数字为了防止重复
         */
        const val SAVE_PATH = "savePath2016050433191"
        val INSTANCE = FileConverter()
    }
    override fun convert(value: ResponseBody): File? {
        val saveFilePath = getSaveFilePath(value)
        return FileUtils.writeResponseBodyToDisk(value, saveFilePath!!)
    }

    private fun getSaveFilePath(value: ResponseBody): String? {
        var saveFilePath: String? = null
        var requestFileName: String? = null
        try {

            //使用反射获得我们自定义的response
            val aClass = value.javaClass
            val field = aClass.getDeclaredField("delegate")
            field.isAccessible = true
            val body = field.get(value) as ResponseBody
            if (body is ProgressResponseBody) {
                val prBody = body
//                saveFilePath = prBody.savePath
//                requestFileName = prBody.fileName
            }

        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }


        //请求的文件名为空则根据时间戳生成一个临时文件名
        if (TextUtils.isEmpty(requestFileName)) {
            requestFileName = System.currentTimeMillis().toString() + ".tmp"
        }

        //如果保存路径是一个文件夹,则在后面加上请求文件名
        if (!TextUtils.isEmpty(saveFilePath)) {
            val file = File(saveFilePath!!)
            if (file.isDirectory) {
                saveFilePath = saveFilePath + File.separator + requestFileName
            }
        }

        //如果保存路径为null则设置默认保存到sdcard根目录
        if (TextUtils.isEmpty(saveFilePath)) {
            saveFilePath = getExternalStorageDirectory().getAbsolutePath() + File.separator + requestFileName
        }

        return saveFilePath
    }
}