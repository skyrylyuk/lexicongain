package com.skyrylyuk.lexicongain

//import retrofit2.RxJavaCallAdapterFactory
import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.skyrylyuk.lexicongain.presenter.TokenPairPresenter
import com.skyrylyuk.lexicongain.util.YandexTranslate
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideFirebaseReference(auth: FirebaseAuth): DatabaseReference {
        val instance = FirebaseDatabase.getInstance()
        instance.setLogLevel(Logger.Level.DEBUG)
        instance.setPersistenceEnabled(true)
        return instance.reference.child(auth.currentUser?.uid)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


}