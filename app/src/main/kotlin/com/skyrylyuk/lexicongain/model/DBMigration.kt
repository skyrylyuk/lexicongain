package com.skyrylyuk.lexicongain.model

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Virtual Solution project
 * Created by skyrylyuk on 11/14/15.
 */
public open class DBMigration : RealmMigration {
    override fun migrate(p0: DynamicRealm?, p1: Long, p2: Long) {
        //        throw UnsupportedOperationException()
    }

    /*
        override fun execute(realm: Realm?, version: Long): Long {
            return 1
        }
    */
}