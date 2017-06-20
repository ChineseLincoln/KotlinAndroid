package unreal.org.ktapp.http.service

import io.reactivex.Observable
import org.unreal.databases.model.UserModel_Table.id
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import unreal.org.ktapp.function.main.data.UserBean

/**
 * <b>类名称：</b> UserService <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 14:27<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
interface UserService{
    @GET("http://192.168.0.138:8080/message/user")
    fun login(@Query("id") id : String ) : Observable<Response<UserBean>>
}