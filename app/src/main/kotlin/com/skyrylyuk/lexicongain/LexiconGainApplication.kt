package com.skyrylyuk.lexicongain

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.evernote.android.job.JobManager
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.DatabaseReference
import timber.log.Timber


/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class LexiconGainApplication : Application() {

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent

        @JvmStatic lateinit var ref: DatabaseReference
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FirebaseTree())
        }


        graph = DaggerApplicationComponent.builder().androidModule(AndroidModule(this)).build()

        JobManager.create(this).addJobCreator { TranslateService() }
        TranslateService.scheduleJob()

    }
}

internal class FirebaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        FirebaseCrash.log(message)

        if (t != null) {
            FirebaseCrash.report(t)
        }
    }
}
