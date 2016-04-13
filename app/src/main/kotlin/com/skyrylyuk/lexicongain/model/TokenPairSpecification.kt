package com.skyrylyuk.lexicongain.model

import io.realm.Realm
import io.realm.RealmResults

/**
 *
 * Created by skyryl on 13.04.16.
 */

class TokenPairSpecification(val originalText: String): RealmSpecification {

    override fun toRealmResults(realm: Realm): RealmResults<TokenPair> {

        return realm.where(TokenPair::class.java)
                .equalTo("originalText", originalText)
                .findAll()
    }

}