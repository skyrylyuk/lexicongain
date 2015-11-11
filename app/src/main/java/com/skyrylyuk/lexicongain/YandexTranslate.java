package com.skyrylyuk.lexicongain;

import com.google.gson.JsonObject;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Virtual Solution project
 * Created by skyrylyuk on 11/11/15.
 */
public interface YandexTranslate {
    String HOST = "https://translate.yandex.net";
    String API_KEY = "trnsl.1.1.20150616T094415Z.ab702c0fb8064c33.a4bb58f089d2c9167ce05c0078ffd982a25f982e";
    String LANG = "en-ru";
    String TEXT = "text";

    @GET("/api/v1.5/tr.json/translate")
    Observable<JsonObject> translate(
            @Query("key") String key,
            @Query("lang") String lang,
            @Query(TEXT) String text
    );

}
