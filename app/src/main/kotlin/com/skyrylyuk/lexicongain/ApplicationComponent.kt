package com.skyrylyuk.lexicongain

import com.skyrylyuk.lexicongain.activity.MainActivity
import com.skyrylyuk.lexicongain.activity.TranslateActivity
import com.skyrylyuk.lexicongain.presenter.IrregularVerbPresenter
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

    fun inject(mainActivity: MainActivity)

    fun inject(translateActivity: TranslateActivity)

    fun inject(irregularVerbPresenter: IrregularVerbPresenter)
}