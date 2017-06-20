package unreal.org.ktapp.function.main

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import org.unreal.core.base.ToolBarActivity
import org.unreal.core.di.component.CoreComponent
import org.unreal.pay.PayFunction
import org.unreal.pay.alipay.AliPay
import org.unreal.pay.payment
import org.unreal.pay.union.UnionBankPay
import org.unreal.pay.weixin.WeiXinPay
import org.unreal.preference.PreferenceManger
import unreal.org.ktapp.R
import unreal.org.ktapp.function.main.component.DaggerMainComponent
import unreal.org.ktapp.function.main.contract.MainContract
import unreal.org.ktapp.function.main.module.MainModule
import unreal.org.ktapp.function.main.sputils.UserSpUtils

/**
 * <b>类名称：</b> MainActivity <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 14:38<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class MainActivity : ToolBarActivity<MainContract.Presenter>(), MainContract.View {

    override fun setTitle(): String = "首页"

    lateinit var payFunction: PayFunction
    lateinit var userSP : UserSpUtils

    override fun injectDagger(coreComponent: CoreComponent) {
        DaggerMainComponent
                .builder()
                .coreComponent(coreComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun bindLayout(): Int = R.layout.activity_main

    override fun afterViews() {
        textView.text = "测试输出"
        //银联支付
        button.setOnClickListener {
            payFunction = payment(UnionBankPay.build {
                activity = this@MainActivity
                tradeCode = "20170603"
                serverModel = UnionBankPay.NORMAL
            },{ toast("支付成功")} ,{ toast(it) })
        }
        //微信支付
        button1.setOnClickListener {
            payFunction = payment(WeiXinPay.build {
                activity = this@MainActivity
                appId = ""
                partnerId = ""
                prepayId = ""
                packageValue = ""
                nonceStr = ""
                timeStamp = ""
                sign = ""
                signType = ""
            },{ toast("支付成功")} ,{ toast(it) })
        }
        //支付宝支付
        button2.setOnClickListener {
            payFunction = payment( AliPay.build {
                activity { this@MainActivity }
                order { "" }
            },{ toast("支付成功")} ,{ toast(it) })
        }
        button3.setOnClickListener {
            presenter.loginLoad("15209687316","123454")

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //支付拦截onActivityResult以便获取支付结果
        payFunction.filterResult(data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun result() {
        toast("登录成功${userSP.userName}")
    }


}