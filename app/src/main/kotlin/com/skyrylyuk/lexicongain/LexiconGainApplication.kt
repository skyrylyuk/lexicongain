package com.skyrylyuk.lexicongain

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class LexiconGainApplication : Application() {

    override fun onCreate() {

        // Setup
        val realmConfig: RealmConfiguration = RealmConfiguration.Builder(applicationContext)
                .name("myrealm.realm")
                .setModules(DBModule())
                .build()

        Realm.setDefaultConfiguration(realmConfig)
    }
}

