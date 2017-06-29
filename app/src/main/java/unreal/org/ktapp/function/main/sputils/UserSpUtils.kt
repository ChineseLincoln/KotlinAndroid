package unreal.org.ktapp.function.main.sputils

import org.unreal.preference.PreferenceManger
import unreal.org.ktapp.function.main.data.UserBean

/**
 * 作者：zhangqiwen
 * 2017/6/20 0020 11:02
 * 名称：
 */
class UserSpUtils {

    companion object {

        private val USER_MOBILE = "userMobile"

        private val USER_PASSWORD = "password"

        private val USER_NAME = "userName"

        private val USER_ID = "userId"
    }

    var userId: String by  PreferenceManger(USER_ID,"")

    var userMobile: String by  PreferenceManger(USER_MOBILE,"")

    var userName: String by  PreferenceManger(USER_NAME,"")

    var userPass: String by PreferenceManger(USER_PASSWORD,"")

    fun saveUser(user: UserBean) {
        userId = user.userId
        userMobile = user.userMobile
        userName = user.userName
        userPass = user.userPassWord
    }

    fun remove (key : String){
        val manger = PreferenceManger<Any>()
        manger.delete(key)
    }
    fun clear() {
        val manger = PreferenceManger<Any>()
        manger.delete()
    }

}