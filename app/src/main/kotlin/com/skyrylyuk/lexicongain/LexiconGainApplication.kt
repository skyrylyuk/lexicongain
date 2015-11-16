package com.skyrylyuk.lexicongain

import android.app.Application
import com.skyrylyuk.lexicongain.model.DBMigration
import com.skyrylyuk.lexicongain.model.DBModule
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
                .migration(DBMigration())
                .build()

        Realm.setDefaultConfiguration(realmConfig)
    }
}

