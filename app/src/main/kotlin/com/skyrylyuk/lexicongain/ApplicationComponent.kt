package com.skyrylyuk.lexicongain

import com.skyrylyuk.lexicongain.activity.*
import com.skyrylyuk.lexicongain.presenter.TokenPairPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */

@Singleton
@Component(modules = arrayOf(AndroidModule::class))
interface  ApplicationComponent {

//    fun inject(application: LexiconGainApplication)

    fun inject(splash: SplashActivity)

    fun inject(main: MainActivity)

    fun inject(translate: TranslateActivity)

    fun inject(translate: AddDialog)

    fun inject(library: LibraryActivity)

    fun inject(tokenPairPresenter: TokenPairPresenter)
}