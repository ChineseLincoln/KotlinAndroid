package org.unreal.update.manger

import android.Manifest
import android.annotation.TargetApi
import android.app.*
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
import me.jessyan.progressmanager.ProgressInfo
import me.jessyan.progressmanager.ProgressManager
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.progressDialog
import org.unreal.update.R
import org.unreal.update.converter.FileUtils
import org.unreal.update.http.helper.RetrofitHelper
import org.unreal.update.http.helper.service.DownloadService
import java.io.File
import android.widget.RemoteViews



/**
 * 作者：zhangqiwen
 * 2017/6/12 0012 17:09
 * 名称：
 */
object DownlaodManger{
    lateinit var context : Activity
    const val AUTHORITY_UPDATE = "org.unreal.update"
    const val UPDATE_DIR = "/unreal/update/"
    lateinit var TYPE : DownLoadType
    lateinit var dialog : ProgressDialog
    lateinit var notificationManager : NotificationManager
    lateinit var notification : Notification
    lateinit var  views: RemoteViews
    lateinit var downloadFileSavePath : String

    fun downloadApk(context: Activity, downloadUrl: String,
                    fileName: String, type : DownLoadType) {
        val rxPermission = RxPermissions(context)
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe{
                    if(it){
                        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                            DownlaodManger.context = context
                            when(type){
                                DownLoadType.Dialog ->{
                                    dialog = context.progressDialog(message = "正在下载，请稍后...",
                                            title = "${context.getString(org.unreal.core.R.string.app_name)}更新")
                                            .apply {
                                                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                                                setCancelable(false)
                                            }
                                }
                                DownLoadType.Notification -> {
                                    notificationManager = context.notificationManager
                                    notification = Notification(R.mipmap.ic_launcher,
                                            "${DownlaodManger.context.getString(org.unreal.core.R.string.app_name)}更新",
                                            System.currentTimeMillis())
                                            .apply {
                                                contentView = RemoteViews(DownlaodManger.context.packageName,
                                                        R.layout.notification_progress)
                                            }
                                }
                            }

                            DownlaodManger.TYPE = type
                            downloadFileSavePath = getDownloadFilePath(fileName)
                            DownlaodManger.retrofitDownload(downloadUrl)
                        } else {
                            Toast.makeText(context, "没有检测到SD卡,请插入SD卡后重新更新应用!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(context, "需要存储权限,请到设置-应用-权限中给予信任!", Toast.LENGTH_LONG).show()
                    }
                }

    }

    private fun retrofitDownload(downloadUrl: String) {
        val downLoadService = RetrofitHelper.createResponseService(DownloadService::class.java)
        downLoadService.downloadWithDynamicUrl(downloadUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ install(context, it) },
                        {it.printStackTrace()})
        ProgressManager.getInstance().addResponseListener(downloadUrl) { info ->
            val percent = FileUtils.div((info.currentbytes.toDouble() / 1024), (info.contentLength.toDouble() / 1024), 2) * 100
            when (TYPE) {
                DownLoadType.Dialog -> showDialog(info)
                DownLoadType.Notification -> showNotification(info,percent.toInt())
            }
        }

    }

    private fun showDialog(info: ProgressInfo) {
        dialog.progress = (info.currentbytes.toDouble()/1024).toInt()
        dialog.max = (info.contentLength.toDouble()/1024).toInt()
        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        if(!dialog.isShowing) {
            dialog.show()
        }
        if(!dialog.isShowing) {
            dialog.show()
        }
        if (info.currentbytes == info.contentLength){
            dialog.dismiss()
        }
    }


    /**
     * 显示通知
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun showNotification(info: ProgressInfo,progress: Int) {
        // 更新状态栏上的下载进度信息
        notification.contentView.setTextViewText(R.id.num_progress, "已下载$progress%")
        notification.contentView.setTextViewText(R.id.not_title,"正在下载...")
        notification.contentView.setProgressBar(R.id.not_progress, 100,
                progress, false)
        notification.contentView.setTextViewText(R.id.format_progress, "${(info.currentbytes.toDouble()/1024).toInt()}KB/${(info.contentLength.toDouble()/1024).toInt()}KB")
        //显示通知
        if (100 == progress){
            notification.contentView.setProgressBar(R.id.not_progress, 100,
                    progress, true)
            notification.contentView.setTextViewText(R.id.not_title,"等待安装")
        }
        notificationManager.notify(0, notification)
    }

    private fun getDownloadFilePath(fileName: String): String {
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
enum class DownLoadType{
    Dialog , Notification
}