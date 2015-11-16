package com.skyrylyuk.lexicongain.model

import io.realm.Realm
import io.realm.RealmMigration

/**
 * Virtual Solution project
 * Created by skyrylyuk on 11/14/15.
 */
public open class DBMigration : RealmMigration {
    override fun execute(realm: Realm?, version: Long): Long {
        return 1
    }
}