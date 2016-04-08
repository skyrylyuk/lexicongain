package com.skyrylyuk.lexicongain.model

import io.realm.Realm
import kotlin.properties.Delegates

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */
class IrregularVerbRepository {

    var realm: Realm by Delegates.notNull()

    init {
        realm = Realm.getDefaultInstance()
    }

    fun getCount(): Long {
        return realm.where(TokenPair::class.java).count()
    }

    fun getNextCard():TokenPair? {
        return realm.where(TokenPair::class.java).findAllSorted("updateDate").firstOrNull()
    }
}