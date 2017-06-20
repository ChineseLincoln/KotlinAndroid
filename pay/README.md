# Step 1 修改包名

修改 org.unreal.pay包下的yourpackagename包修改为App包中的packageName 名称

# Step 2 修改AndroidManifest

修改 Pay Module 模块下 AndroidManifest.xml中

```xml
 <!--yourpackage 替换为你App的项目包名 -->
<activity android:name="yourpackage.wxapi.WXPayEntryActivity"
          android:exported="true"
          android:launchMode="singleTop"
          android:screenOrientation="behind"
          />
```

替换yourpackage修改为App包中的packageName 名称

# Step 3 初始化支付订单信息

## AliPay 支付宝

```kotlin
val aliPay = AliPay.build {
            activity = this@MainActivity
            order = ""
        }
```
或者
```kotlin
val aliPay = AliPay.build {
            activity { this@MainActivity }
            order { "" }
        }
```



## UnionBank 银联

```kotlin
val unionPay = UnionBankPay.build {
            activity = this@MainActivity
            serverModel = UnionBankPay.NORMAL or UnionBankPay.TEST
            tradeCode = ""
        }
```
或者
```kotlin
val unionPay = UnionBankPay.build {
            activity { this@MainActivity }
            serverModel { UnionBankPay.NORMAL or UnionBankPay.TEST }
            tradeCode { "" }
        }
```

## WeiXinPay 微信支付

```kotlin
val weiXinPay = WeiXinPay.build {
            activity = this@MainActivity
            appId = ""
            partnerId = ""
            prepayId = ""
            packageValue = ""
            nonceStr = ""
            timeStamp = ""
            sign = ""
            signType = ""
        }
```
或者
```kotlin
val weiXinPay = WeiXinPay.build {
            activity = this@MainActivity
            appId { "" }
            partnerId { "" }
            prepayId { "" }
            packageValue { "" }
            nonceStr { "" }
            timeStamp { "" }
            sign { "" }
            signType { "" }
        }
```

# Step 4 唤起支付控件，并重写回调方法

```kotlin
// payOrder 是 AliPay 、WeiXinPay、UnionBankPay的抽象接口，因此在点击按钮时，可以直接传递接口的实例化对象进入方法
val payFunction = payment(payOrder, {
            // paySuccess
            println("pay Success")
        }, {
            //payError
            println(it)
        })
```



# Step 5 使用 PayFunction 对象拦截OnActivityResult

```kotlin
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        payFunction.filterResult(data)
        super.onActivityResult(requestCode, resultCode, data)
    }
```



# Samle Code

```kotlin
class MainActivity : ToolBarActivity<MainContract.Presenter>(), MainContract.View {

    override fun setTitle(): String = "首页"

    lateinit var payFunction: PayFunction

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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //支付拦截onActivityResult以便获取支付结果
        payFunction.filterResult(data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
```

