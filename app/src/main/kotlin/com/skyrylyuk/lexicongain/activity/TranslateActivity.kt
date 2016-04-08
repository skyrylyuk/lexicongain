package com.skyrylyuk.lexicongain.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.util.YandexTranslate
import io.realm.Realm
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
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

        val colorDrawable = ColorDrawable(Color.TRANSPARENT)
        window.setBackgroundDrawable(colorDrawable)



        //        setContentView(R.layout.activity_translate)


        if (intent.action == Intent.ACTION_SEND) {
            val original = intent.getStringExtra(Intent.EXTRA_TEXT).trim()

            val alertDialog = AlertDialog.Builder(this).create();
            val inflate = View.inflate(applicationContext, R.layout.activity_translate, null)
            (inflate.findViewById(R.id.txvOriginal) as TextView).text = original
            val prbTranslate = inflate.findViewById(R.id.prbTranslate)
            val txvTranslate = (inflate.findViewById(R.id.txvTranslate) as TextView)
            alertDialog.setView(inflate)
            alertDialog.setOnDismissListener {
                finish()
                overridePendingTransition(0, 0)
            }

            alertDialog.window.setGravity(Gravity.TOP)
            alertDialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            alertDialog.show()

            var restAdapter = Retrofit.Builder()
                    .baseUrl(YandexTranslate.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

            val service = restAdapter.create(YandexTranslate::class.java)

            service.translate(YandexTranslate.API_KEY, YandexTranslate.LANG, original)
                    .observeOn(Schedulers.computation())
                    .map{ response ->
                        response.get(YandexTranslate.TEXT).asString
                    }.observeOn(AndroidSchedulers.mainThread())
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
                    .subscribeOn(Schedulers.io())
                    .subscribe { translation ->
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
