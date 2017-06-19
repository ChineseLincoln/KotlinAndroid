package org.unreal.pay.weixin

import android.app.Activity
import org.unreal.pay.PayOrder

/**
 * <b>类名称：</b> WeiXinPay <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月14日 17:15<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */

class WeiXinPay private constructor(val activity: Activity,
                                    val appId: String,
                                    val partnerId: String,
                                    val prepayId: String,
                                    val packageValue: String,
                                    val nonceStr: String,
                                    val timeStamp: String,
                                    val sign: String,
                                    val signType: String)
    : PayOrder {

    private constructor(builder: Builder) :
            this(builder.activity,
                    builder.appId,
                    builder.partnerId,
                    builder.prepayId,
                    builder.packageValue,
                    builder.nonceStr,
                    builder.timeStamp,
                    builder.sign,
                    builder.signType)

    companion object {
        fun build(init: Builder.() -> Unit) = Builder(init).build()
    }

    class Builder private constructor() {
        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        lateinit var activity: Activity
        lateinit var appId: String
        lateinit var partnerId: String
        lateinit var prepayId: String
        lateinit var packageValue: String
        lateinit var nonceStr: String
        lateinit var timeStamp: String
        lateinit var signType: String
        lateinit var sign: String

        fun activity(init: Builder.() -> Activity) = apply { activity = init() }
        fun appId(init: Builder.() -> String) = apply { appId = init() }
        fun partnerId(init: Builder.() -> String) = apply { partnerId = init() }
        fun prepayId(init: Builder.() -> String) = apply { prepayId = init() }
        fun packageValue(init: Builder.() -> String) = apply { packageValue = init() }
        fun nonceStr(init: Builder.() -> String) = apply { nonceStr = init() }
        fun timeStamp(init: Builder.() -> String) = apply { timeStamp = init() }
        fun signType(init: Builder.() -> String) = apply { signType = init() }
        fun sign(init: Builder.() -> String) = apply { sign = init() }

        fun build() = WeiXinPay(this)
    }
}