package unreal.org.ktapp.function.main

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import org.unreal.core.base.ToolBarActivity
import org.unreal.core.di.component.CoreComponent
import org.unreal.pay.PayFunction
import org.unreal.update.manger.DownLoadType
import unreal.org.ktapp.R
import unreal.org.ktapp.function.main.component. DaggerMainComponent
import unreal.org.ktapp.function.main.contract.MainContract
import unreal.org.ktapp.function.main.module.MainModule

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
class MainActivity : ToolBarActivity<MainContract.Presenter>() , MainContract.View {

    override fun setTitle(): String = "首页"

    lateinit var payFunction : PayFunction

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
        button.setOnClickListener {
            //use databases module
//            UserModel(name = "xiaoli").insert {
//                toast("user save into databases")
//            }
//
//            payFunction = payment(Pay.UnionBankPay.build {
//                activity = this@MainActivity
//                tradeCode = "20170603"
//                serverModel = "01"
//            },{ toast("支付成功")} ,{ toast(it) })
        }
        button1.setOnClickListener {
            org.unreal.update.manger.DownlaodManger.downloadApk(this,
                    "http://183.56.150.169/imtt.dd.qq.com/16891/50172C52EBCCD8F9B0AD2B576DB7BA16.apk?mkey=58fedb8402e16784&f=9602&c=0&fsname=cn.flyexp_2.0.1_6.apk&csr=1bbd&p=.apk",
                    "xiaohui.apk"
                    ,DownLoadType.Notification)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        payFunction.filterResult(data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun result() {
        toast("hello world")
    }


}