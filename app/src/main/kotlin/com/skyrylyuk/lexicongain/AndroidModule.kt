package com.skyrylyuk.lexicongain

import android.app.Application
import android.content.Context
import com.skyrylyuk.lexicongain.model.TokenPairRepository
import com.skyrylyuk.lexicongain.presenter.TokenPairPresenter
import com.skyrylyuk.lexicongain.util.YandexTranslate
import dagger.Module
import dagger.Provides
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import javax.inject.Singleton

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */


@Module
class AndroidModule(private val context: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideRealm(): TokenPairRepository {
        return TokenPairRepository()
    }

    @Provides
    @Singleton
    fun provideService(): YandexTranslate {
        val restAdapter = Retrofit.Builder()
                .baseUrl(YandexTranslate.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

        val service = restAdapter.create(YandexTranslate::class.java)
        return service
    }

    @Provides
    @Singleton
    fun provideIrregularVerbPresenter(): TokenPairPresenter {
        return TokenPairPresenter()
    }
}