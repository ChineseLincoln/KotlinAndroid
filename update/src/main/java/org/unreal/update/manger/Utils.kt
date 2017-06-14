package com.example.administrator.mykotlin.utils

import android.content.Context

/**
 * 作者：zhangqiwen
 * 2017/5/25 0025 10:21
 * 名称：
 */
object Utils {
    private lateinit var context: Context

    fun init(context: Context){
        this.context = context
    }

    fun getContext() : Context {
        return context
    }
}
