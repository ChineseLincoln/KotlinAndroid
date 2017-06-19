package org.unreal.pay.alipay

import android.app.Activity
import org.unreal.pay.PayOrder

/**
 * <b>类名称：</b> AliPay <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月14日 17:16<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class AliPay private constructor(val activity: Activity,
                                 val order: String) : PayOrder{
    constructor(builder : Builder) : this(builder.activity ,
            builder.order)

    companion object{
        fun build(init : Builder.() -> Unit) = Builder(init).build()
    }

    class Builder private constructor(){
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }
        lateinit var activity: Activity
        lateinit var order: String

        fun activity(init: Builder.() -> Activity) = apply { activity = init() }
        fun order(init: Builder.() -> String) = apply { order = init() }

        fun build() = AliPay(this)
    }
}