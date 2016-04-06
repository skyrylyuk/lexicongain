package com.skyrylyuk.lexicongain

import android.app.Application
import com.skyrylyuk.lexicongain.model.DBModule
import com.skyrylyuk.lexicongain.model.IrregularVerb
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class LexiconGainApplication : Application() {

    override fun onCreate() {

        println("TAG ==================================================")
        // Setup
        val realmConfig: RealmConfiguration = RealmConfiguration.Builder(applicationContext)
                .name("lexicon.realm")
                .setModules(DBModule())
                .build()

        Realm.setDefaultConfiguration(realmConfig)

        val instance = Realm.getDefaultInstance()
        if (instance.where(IrregularVerb::class.java).count() == 0L) {
            instance.executeTransaction {
                instance.createAllFromJson(IrregularVerb::class.java, assets.open("IrregularVerb.json"))
            }
        }

    }
}

