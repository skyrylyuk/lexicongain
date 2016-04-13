package com.skyrylyuk.lexicongain.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.model.IrregularVerbRepository
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.util.YandexTranslate
import org.jetbrains.anko.*
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */

class TranslateActivity : Activity() {

    @Inject
    lateinit var repository: IrregularVerbRepository

    var txvOriginalText: TextView by Delegates.notNull()
    var txvTranslateText: TextView by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LexiconGainApplication.graph.inject(this)

        val colorDrawable = ColorDrawable(Color.TRANSPARENT)
        window.setBackgroundDrawable(colorDrawable)


        verticalLayout {
            txvOriginalText = textView {
                backgroundColor = Color.CYAN
                textColor = Color.BLACK
                gravity = Gravity.CENTER_HORIZONTAL
                padding = 15
            }.lparams(width = matchParent, height = wrapContent)
            txvTranslateText = textView {
                backgroundColor = Color.GRAY
                textColor = Color.WHITE
                gravity = Gravity.CENTER_HORIZONTAL
                padding = 10
            }.lparams(width = matchParent, height = wrapContent)
        }

        if (intent.action == Intent.ACTION_SEND) {
            val original = intent.getStringExtra(Intent.EXTRA_TEXT).trim()
            txvOriginalText.text = original
            txvTranslateText.text = original


            var restAdapter = Retrofit.Builder()
                    .baseUrl(YandexTranslate.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

            val service = restAdapter.create(YandexTranslate::class.java)

            service.translate(YandexTranslate.API_KEY, YandexTranslate.LANG, original)
                    .observeOn(Schedulers.computation())
                    .map { response ->
                        response.get(YandexTranslate.TEXT).asString
                    }.observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        txvTranslateText.text = it
                        //                                txvTranslate.visibility = View.VISIBLE
                        //                                prbTranslate.visibility = View.INVISIBLE
                    }
                    .observeOn(Schedulers.immediate())
                    .delay(4, TimeUnit.SECONDS)
                    .doOnNext {
                        finish()
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe { translation ->
                        // Open the default realm


                        val tokenPair = TokenPair().apply {
                            originalText = original
                            translateText = translation
                        }

                        repository.add(tokenPair)

                    }
        }

    }
}
