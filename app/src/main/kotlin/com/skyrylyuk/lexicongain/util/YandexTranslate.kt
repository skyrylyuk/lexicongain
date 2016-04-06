package com.skyrylyuk.lexicongain.util

import com.google.gson.JsonObject
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
interface YandexTranslate {

    @GET("/api/v1.5/tr.json/translate")
    fun translate(
            @Query("key") key: String,
            @Query("lang") lang: String,
            @Query("text") text: String): Observable<JsonObject>

    companion object {
        val HOST = "https://translate.yandex.net"
        val API_KEY = "trnsl.1.1.20150616T094415Z.ab702c0fb8064c33.a4bb58f089d2c9167ce05c0078ffd982a25f982e"
        val LANG = "en-uk"
        val TEXT = "text"
    }

}
