package org.unreal.pay

import android.content.Intent

/**
 * <b>类名称：</b> Pay <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月01日 17:01<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */


interface PayFunction {
    fun checkPluginState(onCheckState: (Boolean) -> Unit)
    fun payment()
    fun filterResult(result: Intent?)
}
