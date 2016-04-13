package com.skyrylyuk.lexicongain

import com.skyrylyuk.lexicongain.activity.LibraryActivity
import com.skyrylyuk.lexicongain.activity.MainActivity
import com.skyrylyuk.lexicongain.activity.TranslateActivity
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

    fun inject(main: MainActivity)

    fun inject(translate: TranslateActivity)

    fun inject(library: LibraryActivity)

    fun inject(tokenPairPresenter: TokenPairPresenter)
}