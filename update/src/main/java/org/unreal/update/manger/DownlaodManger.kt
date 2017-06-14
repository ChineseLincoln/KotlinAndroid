package org.unreal.update.manger

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.NotificationManager
import android.app.ProgressDialog
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
import me.jessyan.progressmanager.ProgressManager
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.progressDialog
import org.unreal.update.R
import org.unreal.update.converter.FileUtils
import org.unreal.update.http.helper.module.ServiceMoudle
import org.unreal.update.http.helper.service.UpdateService
import java.io.File

/**
 * 作者：zhangqiwen
 * 2017/6/12 0012 17:09
 * 名称：
 */
object DownlaodManger{
    lateinit var context : Context
    const val AUTHORITY_UPDATE = "org.unreal.update"
    const val UPDATE_DIR = "/unreal/update/"
    lateinit var TYPE : JessyanDownLoadType
    lateinit var dialog : ProgressDialog
    lateinit var notificationManager : NotificationManager
    lateinit var mNotifyBuilder : NotificationCompat.Builder
    fun downloadApk(context: Activity, downloadUrl: String,
                    fileName: String, type : JessyanDownLoadType) {
        val rxPermission = RxPermissions(context)
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe{
                    if(it){
                        if (createUpdateDirs()) {
                            DownlaodManger.context = context
                            when(type){
                                JessyanDownLoadType.Dialog ->{
                                    DownlaodManger.dialog = context.progressDialog(message = "正在下载，请稍后...",
                                            title = "${context.getString(org.unreal.core.R.string.app_name)}更新")
                                            .apply {
                                                max = 100
                                                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                                                setCancelable(false)
                                            }
                                }
                                JessyanDownLoadType.Notification -> {
                                    DownlaodManger.notificationManager = context.notificationManager
                                }
                            }

                            DownlaodManger.TYPE = type
                            DownlaodManger.retrofitDownload(downloadUrl, DownlaodManger.getUpdateFile(fileName))
                        } else {
                            Toast.makeText(context, "没有检测到SD卡,请插入SD卡后重新更新应用!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, "需要存储权限,请到设置-应用-权限中给予信任!", Toast.LENGTH_LONG).show()
                    }
                }

    }

    private fun retrofitDownload(downloadUrl: String, updateFile: String) {
        val downLoadService = ServiceMoudle.createResponseService(UpdateService::class.java)
        downLoadService.downloadWithDynamicUrl(downloadUrl, updateFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ install(context, it) },
                        {it.printStackTrace()})
        ProgressManager.getInstance().addResponseListener(downloadUrl) { info ->
            val percent = FileUtils.div((info.currentbytes.toDouble() / 1024), (info.contentLength.toDouble() / 1024), 2) * 100
            when (TYPE) {
                JessyanDownLoadType.Dialog -> showDialog(percent.toInt())
                JessyanDownLoadType.Notification -> showNotification(percent.toInt())
            }
        }

    }

    private fun showDialog(progress: Int) {
        dialog.progress = progress
        if(!dialog.isShowing) {
            dialog.show()
        }
        if(!dialog.isShowing) {
            dialog.show()
        }
        if (100 == progress){
            dialog.dismiss()
        }
    }
    /**
     * 显示通知
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun showNotification(progress: Int) {
        mNotifyBuilder = NotificationCompat.Builder(context)
                .setTicker(context.getString(org.unreal.core.R.string.app_name) + "更新")
                .setSmallIcon(R.mipmap.ic_launcher)  //系统状态栏显示的小图标
                .setContentTitle("正在下载...")            //通知栏标题
                .setAutoCancel(false)       //不可点击通知栏的删除按钮删除
                .setWhen(System.currentTimeMillis()) as NotificationCompat.Builder //通知的时间
        if (100 == progress) {
            mNotifyBuilder.setContentTitle("等待安装...")
            mNotifyBuilder.setProgress(100, progress, true)
        } else {
            mNotifyBuilder.setProgress(100, progress, false)
        }
        //显示通知
        notificationManager.notify(0, mNotifyBuilder.build())
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
        filePath.append(UPDATE_DIR)
        filePath.append(File.separator)
        filePath.append(fileName)
        return filePath.toString()
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
}
enum class JessyanDownLoadType{
    Dialog , Notification
}