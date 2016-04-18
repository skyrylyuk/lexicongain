package com.skyrylyuk.lexicongain.model

import io.realm.Realm
import io.realm.RealmResults

/**
 *
 * Created by skyryl on 13.04.16.
 */

class ListTokenPairSpecification(val originalText: String = "") : RealmSpecification {

    override fun toRealmResults(realm: Realm): RealmResults<TokenPair> {

        val realmQuery = realm.where(TokenPair::class.java)

        if (originalText.isNotEmpty()) {
            realmQuery
        }

        return realmQuery
                .findAllSorted("updateDate")
    }

}