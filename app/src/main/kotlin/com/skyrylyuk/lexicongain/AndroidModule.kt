package com.skyrylyuk.lexicongain

import android.app.Application
import android.content.Context
import com.skyrylyuk.lexicongain.model.IrregularVerbRepository
import com.skyrylyuk.lexicongain.presenter.IrregularVerbPresenter
import dagger.Module
import dagger.Provides
import io.realm.Realm
import org.jetbrains.annotations.NotNull
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
    fun provideRealm(): IrregularVerbRepository {
        return IrregularVerbRepository()
    }

    @Provides
    @Singleton
    fun provideIrregularVerbPresenter(): IrregularVerbPresenter {
        return IrregularVerbPresenter()
    }
}