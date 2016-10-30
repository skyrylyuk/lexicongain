package com.skyrylyuk.lexicongain.util

import com.google.firebase.database.DataSnapshot
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.model.TokenPairHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Description
 *
 * Created by skyrylyuk on 10/30/16.
 */


fun DataSnapshot.parse(): TokenPairHolder {
    return TokenPairHolder().apply {
        children?.forEach {
            key = it.key

            value = TokenPair().apply {
                originalText = it.child("originalText").getValue(String::class.java)
                translateText = it.child("translateText").getValue(String::class.java)
                creationDate = it.child("creationDate").getValue(Long::class.java)
                updateDate = it.child("updateDate").getValue(Long::class.java)
                phase = it.child("phase").getValue(Int::class.java)
            }
        }
    }
}

fun Long.toDate(): Date {
    return Date(this)
}

//val format: SimpleDateFormat by lazy { SimpleDateFormat("dd-MM-hh-mm", Locale.US) }
val format: SimpleDateFormat by lazy { SimpleDateFormat("yyyy.MMMM.dd GGG hh:mm aaa", Locale.US) }

fun Long.toDataString(): String {
    return format.format(this.toDate())
}