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
open class TokenPair(

        @PrimaryKey
        open var originalText: String = "",

        open var translateText: String = "",

        open var isIrregularVerb: Boolean = false,

        open var creationDate: Date = Date(),

        open var updateDate: Date = Date(),

        open var phase: Int = 1

) : RealmObject () {

}