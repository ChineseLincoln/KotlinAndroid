package org.unreal.databases

import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import org.unreal.core.base.BaseApplication
import org.unreal.databases.config.UnrealDatabase
import org.unreal.databases.sqlcipher.SQLCipherOpenHelperImpl

/**
 * <b>类名称：</b> DatabaseApplication <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月01日 11:43<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
abstract class DatabaseApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        val flowConfig = FlowConfig
                .Builder(this)
                .addDatabaseConfig(DatabaseConfig.Builder(UnrealDatabase::class.java)
                        .openHelper(::SQLCipherOpenHelperImpl)
                        .build())
                .build()
        FlowManager.init(flowConfig)
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}