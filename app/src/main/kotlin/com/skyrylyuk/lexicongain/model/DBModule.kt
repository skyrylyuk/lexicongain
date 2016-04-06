package com.skyrylyuk.lexicongain.model

import io.realm.annotations.RealmModule

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */

@RealmModule(classes = arrayOf(TokenPair::class, IrregularVerb::class))
open class DBModule {

}
