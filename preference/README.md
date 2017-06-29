## 单值操作

```kotlin
var userId: String by  PreferenceManger(USER_ID,"")
```
## 批量信息操作

```kotlin
//存储app用户信息类
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
    }
```
然后再在存储的地方调用即可
```kotlin
lateinit var userSP : UserSpUtils


val userBean = UserBean(userName, passWord, "1", "110")
userSP.saveUser(userBean)
```

## kotlin信息删除需要注意

```kotlin
//单值删除
fun remove (key : String){
        val manger = PreferenceManger<Any>()
        manger.delete(key)
    }
    
```

```kotlin
//全部删除
fun clear() {
        val manger = PreferenceManger<Any>()
        manger.delete()
    }
```

