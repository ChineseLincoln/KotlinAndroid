package unreal.org.ktapp.function.test

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.rx2.kotlinextensions.list
import com.raizlabs.android.dbflow.rx2.kotlinextensions.rx
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.startActivity
import org.unreal.databases.model.UserModel
import org.unreal.web.WebActivity
import unreal.org.ktapp.R
import unreal.org.ktapp.function.main.MainActivity
import java.util.*

class Main2Activity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        message.setText(R.string.title_home)
                        startActivity<MainActivity>()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_dashboard -> {
                        message.setText(R.string.title_dashboard)
                        val user = UserModel()
                        user.name = "lincoln - "+ Random().nextInt()
                        user.save()
                        select.from(UserModel::class.java)
                                .rx()
                                .list{
                                    it.forEach{
                                        println(it)
                                    }
                                }
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_notifications -> {
                        message.setText(R.string.title_notifications)
                        startActivity<WebActivity>(WebActivity.TITLE to "百度" ,
                                WebActivity.URL to "http://www.baidu.com",
                                WebActivity.HAS_TITLE_BAR to true)
                        return@OnNavigationItemSelectedListener true
                    }
                    else -> return@OnNavigationItemSelectedListener false
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}
