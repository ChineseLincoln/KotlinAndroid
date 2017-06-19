package org.unreal.pay.weixin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.unreal.pay.PayFunction


/**
 * <b>类名称：</b> WeiXinPayImplement <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年06月01日 17:36<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
internal class WeiXinPayImplement(val payOrder: WeiXinPay,
                         val onSuccess: () -> Unit ,
                         val onError: (String) -> Unit)
    : PayFunction {

    private var wxApi =  WXAPIFactory.createWXAPI(payOrder.activity, null)

    lateinit var errorMsg : String

    lateinit var receiver: BroadcastReceiver

    init {
        wxApi.registerApp(payOrder.appId)
        registerLocalBroadCast()
    }


    override fun checkPluginState(onCheckState : (Boolean) -> Unit) {
        var isOk = true
        if (!wxApi.isWXAppInstalled) {
            isOk = false
            errorMsg = "没有安装微信,无法使用微信支付!"
        }
        if (!wxApi.isWXAppSupportAPI) {
            isOk = false
            errorMsg = "当前微信版本过低,无法使用微信支付,请升级微信!"
        }
        onCheckState(isOk)
    }

    override fun payment() {
       checkPluginState{
           if(it){
               val request = PayReq()
               request.appId = payOrder.appId
               request.partnerId = payOrder.partnerId
               request.prepayId = payOrder.prepayId
               request.packageValue = payOrder.packageValue
               request.nonceStr = payOrder.nonceStr
               request.timeStamp = payOrder.timeStamp
               request.sign = payOrder.sign
               request.signType = payOrder.signType
               val isCallState = wxApi.sendReq(request)
               println("isCallState = $isCallState")
           }else{
               onError(errorMsg)
           }
       }

    }

    fun registerLocalBroadCast() {
        //        0	成功	展示成功页面
//        -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//        -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。

        val filter = IntentFilter(WeiXinConfig.LOCAL_BROAD_CAST_ACTION)
        //        0 成功	展示成功页面
//  -1	错误       -2	用户取消
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val errCode = intent.getIntExtra(WeiXinConfig.LOCAL_BROAD_EXTRA_CODE,  -1)
                if (errCode == BaseResp.ErrCode.ERR_OK) { //        0 成功	展示成功页面
                    onSuccess()
                } else {//  -1	错误       -2	用户取消
                    if(errCode == -1) {
                        onError("微信支付错误，请检查参数配置")
                    }else{
                        onError("微信支付失败")
                    }
                }

                receiver.apply {
                    LocalBroadcastManager.getInstance(payOrder.activity).unregisterReceiver(receiver)
                }
            }
        }
        LocalBroadcastManager.getInstance(payOrder.activity).registerReceiver(receiver, filter)
    }

    override fun filterResult(result: Intent?) {
    }

}

internal object WeiXinConfig{
    const val LOCAL_BROAD_CAST_ACTION = "org.unreal.pay.weiXinPayResult"
    const val LOCAL_BROAD_EXTRA_CODE = "StateCode"
}