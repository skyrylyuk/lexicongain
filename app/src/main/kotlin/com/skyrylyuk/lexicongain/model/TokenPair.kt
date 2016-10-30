package com.skyrylyuk.lexicongain.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */

@IgnoreExtraProperties
class TokenPair {
    var originalText: String = ""
    var translateText: String = ""
    var creationDate: Long = System.currentTimeMillis()
    var updateDate: Long = System.currentTimeMillis()
    var phase: Int = 1

    override fun toString(): String {
        return "TokenPair(originalText='$originalText', translateText='$translateText', creationDate=$creationDate, updateDate=$updateDate, phase=$phase)"
    }
}

class TokenPairHolder {
    var key: String = ""
    var value: TokenPair = TokenPair()

    override fun toString(): String {
        return "TokenPairHolder( key='$key', value='$value'"
    }
}
