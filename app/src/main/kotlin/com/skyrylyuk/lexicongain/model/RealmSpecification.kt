package com.skyrylyuk.lexicongain.model

import io.realm.Realm
import io.realm.RealmResults

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */

interface RealmSpecification {

    fun toRealmResults(realm: Realm): RealmResults<TokenPair>
}