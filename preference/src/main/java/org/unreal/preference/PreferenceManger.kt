package org.unreal.preference

import android.content.Context
import org.unreal.core.base.BaseApplication
import java.lang.Long.getLong
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * <b>类名称：</b> PreferenceManger <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> MaTing <br/>
 * <b>修改人：</b> MaTing <br/>
 * <b>修改时间：</b> 2017 年 05 月 24 日 15:24<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class PreferenceManger<T>() : ReadWriteProperty<Any?, T> {
    val context : Context = BaseApplication.coreComponent.application()
    var key: String? = null
    var value: T? = null

    /**
     * 主构造函数
     */
    constructor(name: String, default: T) : this() {
        key = name
        value = default
    }
    val prefs by lazy {
        SecuritySharedPreference(context,"myShared", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(key!!, value!!)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(key!!, value)
    }


    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("The data can not be saved")
        }
        res as U
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("The data can not be saved")
        }.apply()
    }

    fun delete(vararg key: String): Unit {
        if (key.isEmpty()) {
            prefs.edit().clear().commit()
            return
        }
        for (i in 0..key.size) {
            prefs.edit().remove(key[i]).commit()
        }
    }
}