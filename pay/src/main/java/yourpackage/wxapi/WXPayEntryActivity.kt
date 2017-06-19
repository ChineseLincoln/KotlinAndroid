package yourpackage.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.unreal.pay.weixin.WeiXinConfig

/**
 * **类名称：** WXPayEntryActivity <br></br>
 * **类描述：** <br></br>
 * **创建人：** MaTing <br></br>
 * **修改人：** MaTing <br></br>
 * **修改时间：** 2017 年 05 月 25 日 17:46<br></br>
 * **修改备注：** <br></br>

 * @version 1.0.0 <br></br>
 */
class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    private var api: IWXAPI = WXAPIFactory.createWXAPI(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {}

    override fun onResp(resp: BaseResp) {
        /**
         * 微信支付成功回调会开启一个activity，并执行onResp方法，我不希望出现这个界面，所以finish了，在这之前，我发送一个广播
         * 在广播中我做了回调后的操作

         * 如果，你的界面是activity，可以改造这个类为你的回调界面（我的是fragment，所以不用他的回调activity）
         */
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //发送广播，为intent添加的String必须一致，接收广播处
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(WeiXinConfig.LOCAL_BROAD_CAST_ACTION)
                    .putExtra(WeiXinConfig.LOCAL_BROAD_EXTRA_CODE, resp.errCode))
        }
        finish()
    }
}