package org.unreal.preference

import android.content.Context
import java.lang.Long.getLong
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * <b>类名称：</b> Preference <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> MaTing <br/>
 * <b>修改人：</b> MaTing <br/>
 * <b>修改时间：</b> 2017 年 05 月 24 日 15:24<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class Preference <T>(val context: Context, val name: String, val default: T) : ReadWriteProperty<Any?, T> {

    val prefs by lazy {
//        context.getSharedPreferences("xxxx", Context.MODE_PRIVATE)
        SecuritySharedPreference(context,"myShared", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> {
                getLong(name, 0)
            }
            is String -> {
                getString(name, default)
            }
            is Float -> {
                getFloat(name, default)
            }
            is Int -> {
                getInt(name, default)
            }
            is Boolean -> {
                getBoolean(name, default)
            }
            else -> {
                throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }
        res as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = with(prefs.edit()){
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Float -> putFloat(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            else -> {
                throw IllegalArgumentException("This type can't be saved into Preferences")
            }
        }.apply()
    }
}