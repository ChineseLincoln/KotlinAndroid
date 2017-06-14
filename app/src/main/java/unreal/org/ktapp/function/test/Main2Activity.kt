package unreal.org.ktapp.function.test

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.startActivity
import org.unreal.web.WebActivity
import unreal.org.ktapp.R

class Main2Activity : AppCompatActivity() {


    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        message.setText(R.string.title_home)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_dashboard -> {
                        message.setText(R.string.title_dashboard)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_notifications -> {
                        message.setText(R.string.title_notifications)
                        startActivity<WebActivity>(WebActivity.TITLE to "百度" ,
                                WebActivity.URL to "http://www.baidu.com",
                                WebActivity.HAS_TITLE_BAR to true)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}
