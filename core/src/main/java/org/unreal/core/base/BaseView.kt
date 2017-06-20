package org.unreal.core.base

import android.app.Activity
import android.content.Context
import io.reactivex.*
import org.jetbrains.anko.AnkoLogger


/**
 * <b>类名称：</b> BaseView <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 14:43<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */

interface BaseView : AnkoLogger {

    fun showWait()

    fun showWait(message: String)

    fun hideWait(onAnimationEnd: () -> Unit)

    fun closeWait()

    fun finish()

    fun finishAll()

    fun finish(vararg activityClasses: Class<out Activity>)

    fun getContext(): Context

    fun <T> bindToLifecycle(): SingleTransformer<T, T>

}