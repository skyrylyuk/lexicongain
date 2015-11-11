package com.skyrylyuk.lexicongain.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */

@RealmClass
public open class TokenPair(

        @PrimaryKey
        public open var originalText: String = "",

        public open var translateText: String = "",

        public open var crationDate: Date = Date()

) : RealmObject () {

}