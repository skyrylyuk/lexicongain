package com.skyrylyuk.lexicongain.model

import io.realm.Realm
import io.realm.RealmResults

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */
class TokenPairRepository {


    fun add(tokenPair: TokenPair): TokenPair? {
        val realm = Realm.getDefaultInstance()

        var added: TokenPair? = null
        realm.executeTransaction {
            added = realm.copyToRealmOrUpdate(tokenPair)
        }

        realm.close()

        return added
    }

    fun remove(specification: RealmSpecification) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            specification.toRealmResults(realm).clear()
        }

        realm.close()
    }

    fun getNextCard(): TokenPair? {
        val realm = Realm.getDefaultInstance()
        return realm.where(TokenPair::class.java).findAllSorted("updateDate").firstOrNull()
    }

    fun query(): RealmResults<TokenPair> {
        val realm: Realm = Realm.getDefaultInstance()

        return  realm.where(TokenPair::class.java).findAllSorted("updateDate")

        realm.close()
    }
}