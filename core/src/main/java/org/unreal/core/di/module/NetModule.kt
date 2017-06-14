package org.unreal.core.di.module

import android.app.Application
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.ncornette.cache.OkCacheControl
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.unreal.core.configure.DOWNLOAD_FILE_URL
import org.unreal.core.configure.LOCAL_SERVER_URL
import org.unreal.core.di.scope.LocalRetrofit
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.fastjson.FastJsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * <b>类名称：</b> NetModule <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 10:32<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
@Module
class NetModule {

    @Provides
    @Singleton
    fun providerDefaultClient(application: Application): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkCacheControl.on(
                OkHttpClient.Builder()
                        .addNetworkInterceptor(interceptor)
                        .addNetworkInterceptor(StethoInterceptor())
                        .retryOnConnectionFailure(true)
                        .connectTimeout(10, TimeUnit.SECONDS))
                .overrideServerCachePolicy(60)
                .apply() // return to the OkHttpClient.Builder instance
                .cache(Cache(application.cacheDir, 10 * 10 * 1024))
                .build()
    }

    @Provides
    @Singleton
    @LocalRetrofit
    fun providerUserRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(LOCAL_SERVER_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .build()
    }

}