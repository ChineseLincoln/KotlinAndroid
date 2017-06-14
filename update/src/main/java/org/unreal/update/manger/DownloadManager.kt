package org.unreal.update.manger

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.NotificationManager
import android.app.ProgressDialog
import org.jetbrains.anko.*
import android.app.ProgressDialog.STYLE_HORIZONTAL
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.unreal.update.R
import org.unreal.update.converter.FileUtils
import org.unreal.update.http.helper.module.ServiceMoudle
import org.unreal.update.http.helper.service.UpdateService
import org.unreal.update.progress.ProgressResponseListener
import java.io.File


/**
 * 作者：zhangqiwen
 * 2017/6/8 0008 09:57
 * 名称：
 */
object DownloadManager {

    lateinit var context: Context
    const val AUTHORITY_UPDATE = "org.unreal.update"
    const val UPDATE_DIR = "/unreal/update/"
    lateinit var TYPE : DownLoadType
    lateinit var dialog : ProgressDialog
    lateinit var notificationManager : NotificationManager
    lateinit var mNotifyBuilder : NotificationCompat.Builder
    fun downloadApk(context: Activity, downloadUrl: String,
                    fileName: String, type : DownLoadType) {
        val rxPermission = RxPermissions(context)
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe{
                    if(it){
                        if (createUpdateDirs()) {
                            DownloadManager.context = context
                            dialog = context.progressDialog(message = "正在下载，请稍后...",
                                                            title = "${context.getString(org.unreal.core.R.string.app_name)}更新")
                                    .apply {
                                        max = 100
                                        setProgressStyle(STYLE_HORIZONTAL)
                                        setCancelable(false)
                                    }
                            notificationManager = context.notificationManager
                            TYPE = type
                            retrofitDownload(downloadUrl, getUpdateFile(fileName))
                        } else {
                            Toast.makeText(context, "没有检测到SD卡,请插入SD卡后重新更新应用!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, "需要存储权限,请到设置-应用-权限中给予信任!", Toast.LENGTH_LONG).show()
                    }
                }

    }

    private fun createUpdateDirs(): Boolean {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val dirs = File(Environment.getExternalStorageDirectory().absolutePath
                    + File.separator
                    + UPDATE_DIR)
            if (!dirs.exists()) {
                dirs.mkdirs()
            }
            return true
        }
        return false
    }


    private fun getUpdateFile(fileName: String): String {
        val filePath = StringBuilder(Environment.getExternalStorageDirectory().absolutePath)
        filePath.append(File.separator)
        filePath.append(DownloadManager.UPDATE_DIR)
        filePath.append(File.separator)
        filePath.append(fileName)
        return filePath.toString()
    }

    private fun retrofitDownload(downloadUrl: String,
                                 saveFilePath: String) {
        val downloadService = ServiceMoudle.createResponseService(
                UpdateService::class.java,
                object : ProgressResponseListener {
                    override fun onResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean) {
                        val percent = FileUtils.div((bytesRead.toDouble()/1024),(contentLength.toDouble()/1024),2)*100
                        when (TYPE) {
                            DownLoadType.Dialog -> showDialog(percent.toInt(),done)
                            DownLoadType.Notification -> showNotification(percent.toInt(),done)
                        }
                    }
                })
        downloadService.downloadWithDynamicUrl(downloadUrl, saveFilePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ install(context, it) },
                        {it.printStackTrace()})
    }

    private fun showDialog(progress: Int, isSuccess: Boolean) {
        dialog.progress = progress
        if(!dialog.isShowing) {
            dialog.show()
        }
        if (isSuccess) {
            dialog.dismiss()
        }
    }

    /***
     * install app
     */
    private fun install(context: Context, apk: File) {
        if (!apk.exists()) {
            return
        }
        val data: Uri
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //在清单文件中配置
            data = FileProvider.getUriForFile(context, AUTHORITY_UPDATE, apk)
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            data = Uri.fromFile(apk)
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        context.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    /**
     * 显示通知
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun showNotification(progress: Int, isSuccess: Boolean) {
        mNotifyBuilder = NotificationCompat.Builder(context)
                        .setTicker(context.getString(org.unreal.core.R.string.app_name) + "更新")
                        .setSmallIcon(R.mipmap.ic_launcher)  //系统状态栏显示的小图标
                        .setContentTitle("正在下载...")            //通知栏标题
                        .setAutoCancel(false)       //不可点击通知栏的删除按钮删除
                        .setWhen(System.currentTimeMillis()) as NotificationCompat.Builder //通知的时间
        if (isSuccess) {
            mNotifyBuilder.setContentTitle("等待安装...")
            mNotifyBuilder.setProgress(100, progress, true)
        } else {
            mNotifyBuilder.setProgress(100, progress, false)
        }
        //显示通知
        notificationManager.notify(0, mNotifyBuilder.build())
    }

    /**
     * 接受通知
     */
    class NotificationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == "cancelDownload") {
                //处理点击事件
                Toast.makeText(context,"取消下载逻辑",Toast.LENGTH_LONG).show()
            }
        }
    }

}

enum class DownLoadType{
    Dialog , Notification
}