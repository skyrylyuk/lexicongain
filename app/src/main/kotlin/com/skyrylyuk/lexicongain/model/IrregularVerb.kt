package com.skyrylyuk.lexicongain.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 *
 * Created by skyrylyuk on 12/9/15.
 */

@RealmClass
open class IrregularVerb(

        @PrimaryKey
        open var baseForm: String = "",
        open var pastTense: String = "",
        open var pastParticiple: String = ""
) : RealmObject() {

}