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

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    override fun onCreate() {


        // Setup
        val realmConfig: RealmConfiguration = RealmConfiguration.Builder(applicationContext)
                .name("lexicon.realm")
                .modules(DBModule())
                .build()

        Realm.deleteRealm(realmConfig)

        Realm.setDefaultConfiguration(realmConfig)

        val instance = Realm.getDefaultInstance()
        if (instance.where(IrregularVerb::class.java).count() == 0L) {
            instance.executeTransaction {
                instance.createAllFromJson(IrregularVerb::class.java, assets.open("IrregularVerb.json"))
            }
        }

        graph = DaggerApplicationComponent.builder().androidModule(AndroidModule(this)).build()

    }
}

