package org.unreal.pay.union

import android.app.Activity
import org.unreal.pay.PayOrder

/**
 * <b>类名称：</b> UnionBankPay <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月14日 17:16<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class UnionBankPay private constructor(val activity: Activity,
                                       val tradeCode: String,
                                       val serverModel: String) : PayOrder {
    private constructor(builder: Builder) : this(builder.activity,
            builder.tradeCode,
            builder.serverModel)

    companion object {
        fun build(init: Builder.() -> Unit) = Builder(init).build()
        const val NORMAL = "01"
        const val TEST = "00"
    }

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        lateinit var activity: Activity
        lateinit var tradeCode: String
        lateinit var serverModel: String

        fun activity(init: Builder.() -> Activity) = apply { activity = init() }
        fun tradeCode(init: Builder.() -> String) = apply { tradeCode = init() }
        fun serverModel(init: Builder.() -> String) = apply { serverModel = init() }

        fun build() = UnionBankPay(this)
    }
}