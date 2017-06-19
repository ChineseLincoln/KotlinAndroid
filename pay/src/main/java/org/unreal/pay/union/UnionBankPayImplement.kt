package org.unreal.pay.union

import android.content.Intent
import com.unionpay.UPPayAssistEx
import org.unreal.pay.PayFunction

/**
 * <b>类名称：</b> UnionBankPayImplement <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月02日 11:10<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
internal class UnionBankPayImplement(val payOrder: UnionBankPay, val onSuccess: () -> Unit, val onError: (String) -> Unit) : PayFunction {

    override fun filterResult(result: Intent?) {
        if (result == null) {
            return
        }

        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        val resultStatus = result.extras.getString("pay_result")
        when (resultStatus) {
            "success" -> onSuccess()
            "fail" -> onError("银联支付失败")
            "cancel" -> onError("银联支付失败,用户取消了支付")
        }
    }

    override fun checkPluginState(onCheckState: (Boolean) -> Unit) {
        onCheckState(true)
    }

    override fun payment() {
        checkPluginState {
            UPPayAssistEx.startPay (payOrder.activity
                    , null
                    , null
                    , payOrder.tradeCode
                    , payOrder.serverModel)
        }
    }
}