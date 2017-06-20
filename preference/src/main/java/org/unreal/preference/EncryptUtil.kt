package org.unreal.preference

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * **类名称：** EncryptUtil <br></br>
 * **类描述：** <br></br>
 * **创建人：** MaTing <br></br>
 * **修改人：** MaTing <br></br>
 * **修改时间：** 2017 年 05 月 24 日 15:38<br></br>
 * **修改备注：** <br></br>

 * @version 1.0.0 <br></br>
 */
class EncryptUtil private constructor(context: Context) {

    private val key: String


    init {
        val serialNo = getDeviceSerialNumber(context)
        //加密随机字符串生成AES key
        key = SHA("$serialNo#\$ERDTS\$D%F^Gojikbh").substring(0, 16)
        Log.e(TAG, key)
    }

    /**
     * Gets the hardware serial number of this device.

     * @return serial number or Settings.Secure.ANDROID_ID if not available.
     */
    @SuppressLint("HardwareIds")
    private fun getDeviceSerialNumber(context: Context): String {
        try {
            val deviceSerial = Build::class.java.getField("SERIAL").get(null) as String
            when{
                TextUtils.isEmpty(deviceSerial) -> return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                else -> return deviceSerial
            }
        } catch (ignored: Exception) {
            // Fall back  to Android_ID
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

    }


    /**
     * SHA加密
     * @param strText 明文
     * *
     * @return
     */
    private fun SHA(strText: String): String {
        // 返回值
        var strResult: String = ""
        // 是否是有效字符串
        if (strText.isNotEmpty()) {
            try {
                // SHA 加密开始
                val messageDigest = MessageDigest.getInstance("SHA-256")
                // 传入要加密的字符串
                messageDigest.update(strText.toByteArray())
                val byteBuffer = messageDigest.digest()
                val strHexString = StringBuffer()
                byteBuffer.indices.forEach { i ->
                    val hex = Integer.toHexString(0xff and byteBuffer[i].toInt())
                    if (hex.length == 1) {
                        strHexString.append('0')
                    }
                    strHexString.append(hex)
                }
                strResult = strHexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

        }

        return strResult
    }


    /**
     * AES128加密
     * @param plainText 明文
     * *
     * @return
     */
    fun encrypt(plainText: String): String? {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keyspec = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keyspec)
            val encrypted = cipher.doFinal(plainText.toByteArray())
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * AES128解密
     * @param cipherText 密文
     * *
     * @return
     */
    fun decrypt(cipherText: String): String? {
        try {
            val encrypted1 = Base64.decode(cipherText, Base64.NO_WRAP)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keyspec = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.DECRYPT_MODE, keyspec)

            return String(cipher.doFinal(encrypted1))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    companion object {
        lateinit private var instance: EncryptUtil
        private val TAG = EncryptUtil::class.java.simpleName

        /**
         * 单例模式
         * @param context context
         * *
         * @return
         */
        fun getInstance(context: Context): EncryptUtil {
            synchronized(EncryptUtil::class.java) { instance = EncryptUtil(context) }
            return instance
        }
    }
}
