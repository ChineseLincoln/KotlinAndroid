package com.example.administrator.mykotlin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Settings.Secure.getString
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.*
import android.widget.LinearLayout
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * 作者：zhangqiwen
 * 2017/5/26 0026 08:58
 * 名称：
 */
/**
 * 测试扩展函数
 */
fun MutableList<Int>.swap123(x: Int , y: Int): Int {
    var temp = this[0]
    if (x < y){
        return temp + x
    }else{
        return temp + y
    }
}
/**
 * 是否安装
 */
fun String.isMountApp() = Utils.getContext().packageManager.getLaunchIntentForPackage(this) != null
/**
 * 吐司管理
 */
fun Context.toast(message : String,length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, length).show()
}
/**
 * 通知状态栏背景颜色
 */
fun Activity.setTopColor(colorInt: Int){
    if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = calculateStatusColor(colorInt, 112)
    } else if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.addFlags(FLAG_TRANSLUCENT_STATUS)
        val decorView = window.decorView as ViewGroup
        val fakeStatusBarView = decorView.findViewWithTag("FAKE_STATUS_BAR_VIEW_TAG")
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.visibility == View.GONE) {
                fakeStatusBarView.visibility = View.VISIBLE
            }
            fakeStatusBarView.setBackgroundColor(calculateStatusColor(colorInt, 112))
        } else {
            decorView.addView(this.createStatusBarView(colorInt, 112))
        }
        setRootView()
    }
}

/**
 * 设置根布局参数
 */
fun Activity.setRootView() {
    val parent = this.findViewById(android.R.id.content) as ViewGroup
    var i = 0
    val count = parent.childCount
    while (i < count) {
        val childView = parent.getChildAt(i)
        when (childView) {
            is ViewGroup -> {
                childView.run {
                    fitsSystemWindows = true
                    clipToPadding = true
                }
            }
        }
        i++
    }
}
/**
 * 生成一个和状态栏大小相同的半透明矩形条
 *
 */
fun Activity.createStatusBarView(@ColorInt color: Int, alpha: Int): View {
    // 绘制一个和状态栏一样高的矩形
    val statusBarView = View(this)
    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this))
    statusBarView.layoutParams = params
    statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
    statusBarView.tag = "FAKE_STATUS_BAR_VIEW_TAG"
    return statusBarView
}
/**
 * 计算状态栏颜色
 *
 */
fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
    if (alpha == 0) {
        return color
    }
    val a = 1 - alpha / 255f
    var red = color shr 16 and 0xff
    var green = color shr 8 and 0xff
    var blue = color and 0xff
    red = (red * a + 0.5).toInt()
    green = (green * a + 0.5).toInt()
    blue = (blue * a + 0.5).toInt()
    return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
}
/**
 *Adapter实例化Layout
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
/**
 * 启动Activity
 */
inline fun <reified T : Activity> Activity.startActivity(vararg params: Pair<String, String>) {
    val intent = Intent(this, T::class.java)
    params.forEach { intent.putExtra(it.first, it.second) }
    startActivity(intent)
}

/**
 * 安装App
 */
fun File.installApp(authority: String = "") = {
    if (this.exists()) {
        Utils.getContext().startActivity(getInstallAppIntent(authority))
    }
}
/**
 * 获取安装App(支持7.0)的意图
 *
 */
fun File.getInstallAppIntent(authority: String): Intent {
    val intent = Intent(ACTION_VIEW)
    val data: Uri
    val type = "application/vnd.android.package-archive"
    if (SDK_INT < Build.VERSION_CODES.N) {
        data = Uri.fromFile(this)
    } else {
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION
        data = FileProvider.getUriForFile(Utils.getContext(), authority, this)
    }
    intent.setDataAndType(data, type)
    return intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
}
/**
 * 卸载app
 */
fun String.uninstallApp() {
    val intent = Intent(ACTION_DELETE)
    intent.data = Uri.parse("package:$this")
    Utils.getContext().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
}
/**
 * 封装App信息的Bean类
 * @param name        名称
 * *
 * @param icon        图标
 * *
 * @param packageName 包名
 * *
 * @param packagePath 包路径
 * *
 * @param versionName 版本号
 * *
 * @param versionCode 版本码
 * *
 * @param isSystem    是否系统应用
 */
data class AppInfo(val packageName: String, val name: String,
                   val icon: Drawable, val packagePath: String,
                   val versionName: String, val versionCode: Int,
                   val isSystem: Boolean)
/**
 * 获取app信息
 */
fun String.getAppInfo(): AppInfo {
    try {
        val pm = Utils.getContext().packageManager
        val pi = pm.getPackageInfo(this, 0)
        val ai = pi.applicationInfo
        val packageName = pi.packageName
        val name = ai.loadLabel(pm).toString()
        val icon = ai.loadIcon(pm)
        val packagePath = ai.sourceDir
        val versionName = pi.versionName
        val versionCode = pi.versionCode
        val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
        return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return null!!
    }
}
/**
 * 清楚目录下App所有信息
 */
fun String.cleanAppData() {
    cleanCustomCache()
}
/**
 * 清除自定义目录下的文件
 */
fun  String.cleanCustomCache() : Boolean{
    val file = File(this)
    if (file.exists()){
        // 不是目录返回false
        if (!file.isDirectory) return false
        // 现在文件存在且是文件夹
        val files = file.listFiles()
        if (files != null && files.isNotEmpty()) {
            files.forEach {
                when {
                    it.isFile -> when {
                        !deleteFile(it) -> return false
                    }
                    it.isDirectory -> when {
                        !deleteDir(it) -> return false
                    }
                }
            }
        }
    }
    return true
}
fun deleteFile(var0: File?): Boolean {
    return var0 != null && (!var0.exists() || var0.isFile && var0.delete())
}

fun deleteDir(var0: File?): Boolean {
    if (var0 == null) {
        return false
    } else if (!var0.exists()) {
        return true
    } else if (!var0.isDirectory) {
        return false
    } else {
        val var1 = var0.listFiles()
        if (var1 != null && var1.isNotEmpty()) {
            val var2 = var1
            val var3 = var1.size

            for (var4 in 0..var3 - 1) {
                val var5 = var2[var4]
                if (var5.isFile) {
                    if (!deleteFile(var5)) {
                        return false
                    }
                } else if (var5.isDirectory && !deleteDir(var5)) {
                    return false
                }
            }
        }

        return var0.delete()
    }
}
/**
 *  获取跳至拨号界面意图
 *  String(电话号码)
 */
fun String.getDialIntent(): Intent {
    val intent = Intent(ACTION_DIAL, Uri.parse("tel:$this"))
    return intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
}
/**
 * 获取拍照的意图
 */
fun Uri.getCaptureIntent(): Intent {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, this)
    return intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
}
/**
 * 为滑动返回界面添加状态栏颜色
 */
fun Activity.setColorForSwipeBack(color: Int) {
    when {
        SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            val contentView = this.findViewById(android.R.id.content) as ViewGroup
            val rootView = contentView.getChildAt(0)
            val statusBarHeight = getStatusBarHeight(this)
            when (rootView) {
                is CoordinatorLayout -> {
                    val coordinatorLayout = rootView
                    when {
                        SDK_INT < Build.VERSION_CODES.LOLLIPOP -> {
                            coordinatorLayout.setFitsSystemWindows(false)
                            contentView.setBackgroundColor(calculateStatusColor(color, 112))
                            val isNeedRequestLayout = contentView.paddingTop < statusBarHeight
                            when {
                                isNeedRequestLayout -> {
                                    contentView.setPadding(0, statusBarHeight, 0, 0)
                                    coordinatorLayout.post({ coordinatorLayout.requestLayout() })
                                }
                            }
                        }
                        else -> coordinatorLayout.setStatusBarBackgroundColor(calculateStatusColor(color, 112))
                    }
                }
                else -> {
                    contentView.setPadding(0, statusBarHeight, 0, 0)
                    contentView.setBackgroundColor(calculateStatusColor(color, 112))
                }
            }
            setTransparentForWindow()
        }
    }
}

fun getStatusBarHeight(var0: Context): Int {
    val var1 = var0.resources.getIdentifier("status_bar_height", "dimen", "android")
    return var0.resources.getDimensionPixelSize(var1)
}
/**
 * 针对根布局是 CoordinatorLayout, 使状态栏半透明
 * 适用于图片作为背景的界面,此时需要图片填充到状态栏
 * app引导页面可能用到
 */
fun Activity.setTransLenct(){
    if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
        when {
            SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
                window.run {
                addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                clearFlags(FLAG_TRANSLUCENT_STATUS)
                addFlags(FLAG_TRANSLUCENT_NAVIGATION)
                statusBarColor = Color.TRANSPARENT
                }
            else -> window.addFlags(FLAG_TRANSLUCENT_STATUS)
        }
        setRootView()
        addTranslucentView(112)
    }
}
 /**
  * 添加半透明矩形条
  *
 */
fun Activity.addTranslucentView(statusBarAlpha : Int) {
    val contentView = this.findViewById(android.R.id.content) as ViewGroup
    val fakeTranslucentView = contentView.findViewWithTag("FAKE_TRANSLUCENT_VIEW_TAG")
     fakeTranslucentView.run {
         when (visibility) {
             View.GONE -> visibility = View.VISIBLE
         }
         setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0))
     }

}

/**
 * 设置透明
 */
private fun Activity.setTransparentForWindow() {
    if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    } else if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.setFlags(FLAG_TRANSLUCENT_STATUS, FLAG_TRANSLUCENT_STATUS)
    }
}
/**
 * 清除内部sp
 */
fun Context.cleanExternalSP() = this.filesDir.parent + File.separator + "shared_prefs".cleanCustomCache()
/**
 * 根据名称清除数据库
 */
fun Context.cleanInternalDbByName(dbName : String) = this.deleteDatabase(dbName)
/**
 * 获取设备系统版本号
 */
fun getSDKVersion() =  Build.VERSION.SDK_INT
/**
 *获取设备AndroidID
 */
fun Context.getAndroidID() = getString(this.contentResolver, Settings.Secure.ANDROID_ID)
/**
 * 获取设备厂商
 */
fun getManufacturer() = Build.MANUFACTURER
/**
 *bitmap转byteArray
 */
fun Bitmap.bitmap2Bytes(format : Bitmap.CompressFormat): ByteArray {
    val baos = ByteArrayOutputStream()
    this.compress(format, 100, baos)
    return baos.toByteArray()
}
/**
 * byteArray转bitmap
 */
fun ByteArray.bytes2Bitmap() = BitmapFactory.decodeByteArray(this, 0, this.size)

/**
 * 缩放图片
 */
fun Bitmap.scale(newWidth : Int , netHeight : Int): Bitmap {
    val ret = Bitmap.createScaledBitmap(this,newWidth,netHeight,true)
    this.recycle()
    return ret
}
/**
 * 缩放图片(倍数缩放)
 */
fun Bitmap.scale(scaleWidth : Float , scaleHeight : Float ) : Bitmap{
    val  matrix = Matrix()
    matrix.setScale(scaleWidth,scaleHeight)
    val ret = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    this.recycle()
    return  ret
}
/**
 * 转为圆形图片
 */
fun  Bitmap.toRound(recycle : Boolean) : Bitmap{
    val width = this.width
    val height = this.height
    val radius = Math.min(width, height) shr 1
    val ret = Bitmap.createBitmap(width, height, this.config)
    val paint = Paint()
    val canvas = Canvas(ret)
    val rect = Rect(0, 0, width, height)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    canvas.drawCircle((width shr 1).toFloat(), (height shr  1).toFloat(), radius.toFloat(), paint)
    paint.xfermode =  PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    if (recycle && !this.isRecycled) {
        this.recycle()
    }
    return ret
}
/**
 * 转为圆角图片
 */
fun Bitmap.toRoundCorner(radius : Float,recycle: Boolean) : Bitmap{
    val width = this.width
    val height = this.height
    val ret = Bitmap.createBitmap(width, height, this.config)
    val paint = Paint()
    val canvas = Canvas(ret)
    val rect = Rect(0, 0, width, height)
    paint.isAntiAlias = true
    canvas.drawRoundRect(RectF(rect), radius, radius, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    if (recycle && !this.isRecycled)
        this.recycle()
    return ret
}
/**
 * 添加图片水印
 */
fun Bitmap.addImageWatermark(watermark : Bitmap ,x : Float,y : Float, alpha : Int,recycle :  Boolean ) : Bitmap{
    val ret = this.copy(this.config, true)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(ret)
    paint.alpha = alpha
    canvas.drawBitmap(watermark, x, y, paint)
    if (recycle && !this.isRecycled) this.recycle()
    return ret
}
/**
 * 添加文字水印
 */
fun Bitmap.addTextWatermark(content : String , textSize : Float
                            , color: Int,x: Float,y: Float
                            ,recycle: Boolean) : Bitmap{
    val ret = this.copy(this.config, true)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(ret)
    paint.color = color
    paint.textSize = textSize
    val bounds = Rect()
    paint.getTextBounds(content, 0, content.length, bounds)
    canvas.drawText(content, x, y + textSize, paint)
    if (recycle && !this.isRecycled) this.recycle()
    return ret
}
/**
 * 压缩图片(质量压缩)
 */
fun Bitmap.compressByQuality(quality : Int,recycle : Boolean) : Bitmap{
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, baos)
    val bytes = baos.toByteArray()
    if (recycle && !this.isRecycled) this.recycle()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

/**
 * 农历转换
 */
//|----4位闰月|-------------13位1为30天，0为29天|
private val lunar_month_days = intArrayOf(1887, 0x1694, 0x16aa, 0x4ad5, 0xab6, 0xc4b7, 0x4ae, 0xa56, 0xb52a, 0x1d2a, 0xd54, 0x75aa, 0x156a, 0x1096d, 0x95c, 0x14ae, 0xaa4d, 0x1a4c, 0x1b2a, 0x8d55, 0xad4, 0x135a, 0x495d, 0x95c, 0xd49b, 0x149a, 0x1a4a, 0xbaa5, 0x16a8, 0x1ad4, 0x52da, 0x12b6, 0xe937, 0x92e, 0x1496, 0xb64b, 0xd4a, 0xda8, 0x95b5, 0x56c, 0x12ae, 0x492f, 0x92e, 0xcc96, 0x1a94, 0x1d4a, 0xada9, 0xb5a, 0x56c, 0x726e, 0x125c, 0xf92d, 0x192a, 0x1a94, 0xdb4a, 0x16aa, 0xad4, 0x955b, 0x4ba, 0x125a, 0x592b, 0x152a, 0xf695, 0xd94, 0x16aa, 0xaab5, 0x9b4, 0x14b6, 0x6a57, 0xa56, 0x1152a, 0x1d2a, 0xd54, 0xd5aa, 0x156a, 0x96c, 0x94ae, 0x14ae, 0xa4c, 0x7d26, 0x1b2a, 0xeb55, 0xad4, 0x12da, 0xa95d, 0x95a, 0x149a, 0x9a4d, 0x1a4a, 0x11aa5, 0x16a8, 0x16d4, 0xd2da, 0x12b6, 0x936, 0x9497, 0x1496, 0x1564b, 0xd4a, 0xda8, 0xd5b4, 0x156c, 0x12ae, 0xa92f, 0x92e, 0xc96, 0x6d4a, 0x1d4a, 0x10d65, 0xb58, 0x156c, 0xb26d, 0x125c, 0x192c, 0x9a95, 0x1a94, 0x1b4a, 0x4b55, 0xad4, 0xf55b, 0x4ba, 0x125a, 0xb92b, 0x152a, 0x1694, 0x96aa, 0x15aa, 0x12ab5, 0x974, 0x14b6, 0xca57, 0xa56, 0x1526, 0x8e95, 0xd54, 0x15aa, 0x49b5, 0x96c, 0xd4ae, 0x149c, 0x1a4c, 0xbd26, 0x1aa6, 0xb54, 0x6d6a, 0x12da, 0x1695d, 0x95a, 0x149a, 0xda4b, 0x1a4a, 0x1aa4, 0xbb54, 0x16b4, 0xada, 0x495b, 0x936, 0xf497, 0x1496, 0x154a, 0xb6a5, 0xda4, 0x15b4, 0x6ab6, 0x126e, 0x1092f, 0x92e, 0xc96, 0xcd4a, 0x1d4a, 0xd64, 0x956c, 0x155c, 0x125c, 0x792e, 0x192c, 0xfa95, 0x1a94, 0x1b4a, 0xab55, 0xad4, 0x14da, 0x8a5d, 0xa5a, 0x1152b, 0x152a, 0x1694, 0xd6aa, 0x15aa, 0xab4, 0x94ba, 0x14b6, 0xa56, 0x7527, 0xd26, 0xee53, 0xd54, 0x15aa, 0xa9b5, 0x96c, 0x14ae, 0x8a4e, 0x1a4c, 0x11d26, 0x1aa4, 0x1b54, 0xcd6a, 0xada, 0x95c, 0x949d, 0x149a, 0x1a2a, 0x5b25, 0x1aa4, 0xfb52, 0x16b4, 0xaba, 0xa95b, 0x936, 0x1496, 0x9a4b, 0x154a, 0x136a5, 0xda4, 0x15ac)

private val solar_1_1 = intArrayOf(1887, 0xec04c, 0xec23f, 0xec435, 0xec649, 0xec83e, 0xeca51, 0xecc46, 0xece3a, 0xed04d, 0xed242, 0xed436, 0xed64a, 0xed83f, 0xeda53, 0xedc48, 0xede3d, 0xee050, 0xee244, 0xee439, 0xee64d, 0xee842, 0xeea36, 0xeec4a, 0xeee3e, 0xef052, 0xef246, 0xef43a, 0xef64e, 0xef843, 0xefa37, 0xefc4b, 0xefe41, 0xf0054, 0xf0248, 0xf043c, 0xf0650, 0xf0845, 0xf0a38, 0xf0c4d, 0xf0e42, 0xf1037, 0xf124a, 0xf143e, 0xf1651, 0xf1846, 0xf1a3a, 0xf1c4e, 0xf1e44, 0xf2038, 0xf224b, 0xf243f, 0xf2653, 0xf2848, 0xf2a3b, 0xf2c4f, 0xf2e45, 0xf3039, 0xf324d, 0xf3442, 0xf3636, 0xf384a, 0xf3a3d, 0xf3c51, 0xf3e46, 0xf403b, 0xf424e, 0xf4443, 0xf4638, 0xf484c, 0xf4a3f, 0xf4c52, 0xf4e48, 0xf503c, 0xf524f, 0xf5445, 0xf5639, 0xf584d, 0xf5a42, 0xf5c35, 0xf5e49, 0xf603e, 0xf6251, 0xf6446, 0xf663b, 0xf684f, 0xf6a43, 0xf6c37, 0xf6e4b, 0xf703f, 0xf7252, 0xf7447, 0xf763c, 0xf7850, 0xf7a45, 0xf7c39, 0xf7e4d, 0xf8042, 0xf8254, 0xf8449, 0xf863d, 0xf8851, 0xf8a46, 0xf8c3b, 0xf8e4f, 0xf9044, 0xf9237, 0xf944a, 0xf963f, 0xf9853, 0xf9a47, 0xf9c3c, 0xf9e50, 0xfa045, 0xfa238, 0xfa44c, 0xfa641, 0xfa836, 0xfaa49, 0xfac3d, 0xfae52, 0xfb047, 0xfb23a, 0xfb44e, 0xfb643, 0xfb837, 0xfba4a, 0xfbc3f, 0xfbe53, 0xfc048, 0xfc23c, 0xfc450, 0xfc645, 0xfc839, 0xfca4c, 0xfcc41, 0xfce36, 0xfd04a, 0xfd23d, 0xfd451, 0xfd646, 0xfd83a, 0xfda4d, 0xfdc43, 0xfde37, 0xfe04b, 0xfe23f, 0xfe453, 0xfe648, 0xfe83c, 0xfea4f, 0xfec44, 0xfee38, 0xff04c, 0xff241, 0xff436, 0xff64a, 0xff83e, 0xffa51, 0xffc46, 0xffe3a, 0x10004e, 0x100242, 0x100437, 0x10064b, 0x100841, 0x100a53, 0x100c48, 0x100e3c, 0x10104f, 0x101244, 0x101438, 0x10164c, 0x101842, 0x101a35, 0x101c49, 0x101e3d, 0x102051, 0x102245, 0x10243a, 0x10264e, 0x102843, 0x102a37, 0x102c4b, 0x102e3f, 0x103053, 0x103247, 0x10343b, 0x10364f, 0x103845, 0x103a38, 0x103c4c, 0x103e42, 0x104036, 0x104249, 0x10443d, 0x104651, 0x104846, 0x104a3a, 0x104c4e, 0x104e43, 0x105038, 0x10524a, 0x10543e, 0x105652, 0x105847, 0x105a3b, 0x105c4f, 0x105e45, 0x106039, 0x10624c, 0x106441, 0x106635, 0x106849, 0x106a3d, 0x106c51, 0x106e47, 0x10703c, 0x10724f, 0x107444, 0x107638, 0x10784c, 0x107a3f, 0x107c53, 0x107e48)

private fun GetBitInt(data: Int, length: Int, shift: Int): Int {
    return data and ((1 shl length) - 1 shl shift) shr shift
}

/**
 * 从1582年10月开始算
 */
private fun SolarToInt(y: Int, m: Int, d: Int): Long {
    var y = y
    var m = m
    m = (m + 9) % 12
    y -= m / 10
    return (365 * y + y / 4 - y / 100 + y / 400 + (m * 306 + 5) / 10 + (d - 1)).toLong()
}

/**
 * 农历年份
 */
fun Int.lunarYearToGanZhi(): String {
    val tianGan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    val diZhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
    return tianGan[(this - 4) % 10] + diZhi[(this - 4) % 12] + "年"
}

fun SolarFromInt(g: Long): Solar {
    var y = (10000 * g + 14780) / 3652425
    var ddd = g - (365 * y + y / 4 - y / 100 + y / 400)
    if (ddd < 0) {
        y--
        ddd = g - (365 * y + y / 4 - y / 100 + y / 400)
    }
    val mi = (100 * ddd + 52) / 3060
    val mm = (mi + 2) % 12 + 1
    y = y + (mi + 2) / 12
    val dd = ddd - (mi * 306 + 5) / 10 + 1
    val solar = Solar()
    solar.solarYear = y.toInt()
    solar.solarMonth = mm.toInt()
    solar.solarDay = dd.toInt()
    return solar
}

/**
 * 农历转公历
 */
fun Lunar.LunarToSolar(): Solar {
    val days = lunar_month_days[this.lunarYear - lunar_month_days[0]]
    val leap = GetBitInt(days, 4, 13)
    var loopend = leap
    when {
        !this.isLeap -> when {
            this.lunarMonth <= leap || leap == 0 -> loopend = this.lunarMonth - 1
            else -> loopend = this.lunarMonth
        }
    }
    var offset = (0..loopend - 1).sumBy { if (GetBitInt(days, 1, 12 - it) == 1) 30 else 29 }
    offset += this.lunarDay

    val solar11 = solar_1_1[this.lunarYear - solar_1_1[0]]

    val y = GetBitInt(solar11, 12, 9)
    val m = GetBitInt(solar11, 4, 5)
    val d = GetBitInt(solar11, 5, 0)

    return SolarFromInt(SolarToInt(y, m, d) + offset - 1)
}

/**
 * 公历转农历
 */
fun Solar.SolarToLunar(): Lunar {
    val lunar = Lunar()
    var index = this.solarYear - solar_1_1[0]
    val data = this.solarYear shl 9 or (this.solarMonth shl 5) or this.solarDay
    var solar11 = 0
    if (solar_1_1[index] > data) {
        index--
    }
    solar11 = solar_1_1[index]
    val y = GetBitInt(solar11, 12, 9)
    val m = GetBitInt(solar11, 4, 5)
    val d = GetBitInt(solar11, 5, 0)
    var offset = SolarToInt(this.solarYear, this.solarMonth, this.solarDay) - SolarToInt(y, m, d)

    val days = lunar_month_days[index]
    val leap = GetBitInt(days, 4, 13)

    val lunarY = index + solar_1_1[0]
    var lunarM = 1
    var lunarD = 1
    offset += 1

    for (i in 0..12) {
        val dm = if (GetBitInt(days, 1, 12 - i) == 1) 30 else 29
        if (offset > dm) {
            lunarM++
            offset -= dm.toLong()
        } else {
            break
        }
    }
    lunarD = offset.toInt()
    lunar.lunarYear = lunarY
    lunar.lunarMonth = lunarM
    lunar.isLeap = false
    if (leap != 0 && lunarM > leap) {
        lunar.lunarMonth = lunarM - 1
        if (lunarM == leap + 1) {
            lunar.isLeap = true
        }
    }

    lunar.lunarDay = lunarD
    return lunar
}

class Lunar {
    var isLeap: Boolean = false
    var lunarDay: Int = 0
    var lunarMonth: Int = 0
    var lunarYear: Int = 0
}

class Solar {
    var solarDay: Int = 0
    var solarMonth: Int = 0
    var solarYear: Int = 0
}

