package org.unreal.core.base

import android.app.Application
import com.facebook.stetho.Stetho
import org.jetbrains.anko.AnkoLogger
import org.unreal.core.di.component.CoreComponent
import org.unreal.core.di.component.DaggerCoreComponent
import org.unreal.core.di.module.CoreModule


abstract class BaseApplication : Application() , AnkoLogger {

    override fun onCreate() {
        super.onCreate()
        initBaseComponent()
        initStetho()
        initApplication()
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }

    private fun initBaseComponent() {
        coreComponent = DaggerCoreComponent
                .builder()
                .coreModule(CoreModule(this))
                .build()
    }

    companion object {
        lateinit var coreComponent: CoreComponent
    }

    abstract fun initApplication()
}