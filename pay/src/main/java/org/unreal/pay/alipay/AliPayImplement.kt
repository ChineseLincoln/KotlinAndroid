package org.unreal.pay.alipay

import android.content.Intent
import com.alipay.sdk.app.PayTask
import org.unreal.pay.PayFunction


/**
 * <b>类名称：</b> AliPayImplement <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月02日 10:24<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
internal class AliPayImplement(val payOrder: AliPay, val onSuccess: () -> Unit, val onError: (String) -> Unit) : PayFunction {

    override fun payment() {
        checkPluginState {
            if (it) {
                val payTask = PayTask(payOrder.activity)
                // 调用查询接口，获取查询结果
                val result = PayResult(payTask.payV2(payOrder.order, true))
                payOrder.activity.runOnUiThread {
                    when (result.resultStatus) {
                        "9000" -> onSuccess()
                        "8000" -> onError("支付宝支付结果确认中")
                        else -> onError("支付宝支付失败")
                    }
                }
            } else {
                payOrder.activity.runOnUiThread {
                    onError("没有安装支付宝,无法使用支付宝支付!")
                }
            }
        }
    }

    override fun checkPluginState(onCheckState: (Boolean) -> Unit) {

        val checkRunnable = Runnable {
            // 构造PayTask 对象
            val payTask = PayTask(payOrder.activity)
            // 调用查询接口，获取查询结果
            val result = payTask.version
            if (result.isEmpty()) {
                onCheckState(false)
            } else {
                onCheckState(true)
            }
        }

        val payThread = Thread(checkRunnable)
        payThread.start()
    }

    override fun filterResult(result: Intent?) {
    }

}

data class PayResult(val map: Map<String, String>) {
    val resultStatus = map["resultStatus"]
    val result = map["result"]
    val memo = map["memo"]
}