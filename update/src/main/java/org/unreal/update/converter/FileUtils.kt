package org.unreal.update.converter

import okhttp3.ResponseBody
import java.io.*
import java.math.BigDecimal


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 16:31
 * 名称：
 */
object FileUtils{
    /**
     * 将文件写入本地
     * @param body http响应体
     * *
     * @param path 保存路径
     * *
     * @return 保存file
     */
    fun writeResponseBodyToDisk(body: ResponseBody, path: String): File? {

        val saveFile : File
        try {
            saveFile = File(path)
            createDirs(saveFile)
            val inputStream: InputStream
            val outputStream: OutputStream
            try {
                val fileReader = ByteArray(4096)
                inputStream = body.byteStream()
                outputStream = FileOutputStream(saveFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
                return saveFile
            } catch (e: IOException) {
                return saveFile
            }
        } catch (e: IOException) {
            return null
        }

    }

    fun createDirs(file: File) {
        val dir = file.parent
        createDirs(dir)
    }

    fun createDirs(dirPath: String) {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     */
    fun div(v1: Double, v2: Double, scale: Int): Double {
        if (scale < 0) {
            throw IllegalArgumentException(
                    "The scale must be a positive integer or zero")
        }
        val b1 = BigDecimal(java.lang.Double.toString(v1))
        val b2 = BigDecimal(java.lang.Double.toString(v2))
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}