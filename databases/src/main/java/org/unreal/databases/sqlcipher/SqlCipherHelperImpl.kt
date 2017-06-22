package org.unreal.databases.sqlcipher

import com.raizlabs.android.dbflow.config.DatabaseDefinition
import com.raizlabs.android.dbflow.sqlcipher.SQLCipherOpenHelper
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener



/**
 * <b>类名称：</b> SqlCipherHelperImpl <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月01日 13:43<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class SQLCipherOpenHelperImpl(databaseDefinition: DatabaseDefinition, helperListener: DatabaseHelperListener?)
    : SQLCipherOpenHelper(databaseDefinition, helperListener) {

    override fun getCipherSecret() = "lincoln@unreal.org"
}