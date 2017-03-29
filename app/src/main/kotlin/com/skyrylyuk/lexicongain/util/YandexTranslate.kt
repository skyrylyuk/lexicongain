package com.skyrylyuk.lexicongain.util

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
interface YandexTranslate {

    @GET("/api/v1.5/tr.json/translate")
    fun translate(
            @Query("key") key: String = API_KEY_TRNSL,
            @Query("lang") lang: String = LANG,
            @Query("text") text: String): Observable<JsonObject>

    @GET("/api/v1.5/tr.json/detect")
    fun detect(
            @Query("key") key: String = API_KEY_TRNSL,
            //            @Query("hint") hint: String = HINT,
            @Query("text") text: String): Observable<JsonObject>

    companion object {
        val HOST = "https://translate.yandex.net"
        val API_KEY_TRNSL = "trnsl.1.1.20150616T094415Z.ab702c0fb8064c33.a4bb58f089d2c9167ce05c0078ffd982a25f982e"
        val API_KEY_DICT = "dict.1.1.20160510T094313Z.dec514735c4ef13b.219581b270abf4e84253810433e4cd61df484a5d"
        val LANG = "en-uk"
        val HINT = "hint"
        val TEXT = "text"
    }

}
