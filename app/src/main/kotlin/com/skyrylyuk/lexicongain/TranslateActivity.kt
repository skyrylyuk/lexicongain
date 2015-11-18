package com.skyrylyuk.lexicongain

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.gson.JsonObject
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import retrofit.RestAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */

class TranslateActivity : Activity() {

    companion object {
        val TAG = TranslateActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (intent.action == Intent.ACTION_SEND) {
            val original = intent.getStringExtra(Intent.EXTRA_TEXT).trim()

            val alertDialog = AlertDialog.Builder(this, R.style.Base_V21_Theme_AppCompat_Light).create();
            val inflate = View.inflate(applicationContext, R.layout.activity_translate, null)
            (inflate.findViewById(R.id.txvOriginal) as TextView).text = original
            val prbTranslate = inflate.findViewById(R.id.prbTranslate)
            val txvTranslate = (inflate.findViewById(R.id.txvTranslate) as TextView)
            alertDialog.setView(inflate)
            alertDialog.setOnDismissListener {
                finish()
            }

            alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            alertDialog.show()

            var restAdapter = RestAdapter.Builder()
                    .setEndpoint(YandexTranslate.HOST)
                    .build()

            val service = restAdapter.create(YandexTranslate::class.java)

            service.translate(YandexTranslate.API_KEY, YandexTranslate.LANG, original)
                    .observeOn(Schedulers.computation())
                    .map(object : Func1<JsonObject, String> {
                        override fun call(response: JsonObject): String {
                            return response.get(YandexTranslate.TEXT).asString
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        txvTranslate.text = it
                        txvTranslate.visibility = View.VISIBLE
                        prbTranslate.visibility = View.INVISIBLE
                    }
                    .observeOn(Schedulers.immediate())
                    .delay(4, TimeUnit.SECONDS)
                    .doOnNext {
                        alertDialog.dismiss()
                    }
                    .subscribeOn(Schedulers.trampoline())
                    .subscribe { translation ->
                        // TODO extract save to separate function

                        // Open the default realm
                        var realm = Realm.getDefaultInstance()

                        // All writes must be wrapped in a transaction to facilitate safe multi threading
                        realm.beginTransaction()

                        val tokenPair = TokenPair().apply {
                            originalText = original
                            translateText = translation
                        }

                        realm.copyToRealmOrUpdate(tokenPair)

                        // When the transaction is committed, all changes a synced to disk.
                        realm.commitTransaction()
                    }
        }
    }
}
