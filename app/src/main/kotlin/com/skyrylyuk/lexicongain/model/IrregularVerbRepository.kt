package com.skyrylyuk.lexicongain.model

import io.realm.Realm

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */
class IrregularVerbRepository {


    fun add(tokenPair: TokenPair) {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            realm.copyToRealmOrUpdate(tokenPair)
        }
    }

    fun getNextCard(): TokenPair? {
        val realm = Realm.getDefaultInstance()
        return realm.where(TokenPair::class.java).findAllSorted("updateDate").firstOrNull()
    }
}