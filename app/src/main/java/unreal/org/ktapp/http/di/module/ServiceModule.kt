package unreal.org.ktapp.http.di.module

import dagger.Module
import dagger.Provides
import org.unreal.core.di.scope.LocalRetrofit
import org.unreal.core.di.scope.NetScope
import retrofit2.Retrofit
import unreal.org.ktapp.http.service.UserService

/**
 * <b>类名称：</b> RetrofitHelper <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 14:26<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
@Module
class ServiceModule {

    @NetScope
    @Provides
    fun providerUserService(@LocalRetrofit retrofit: Retrofit)
            = retrofit.create(UserService::class.java)
}