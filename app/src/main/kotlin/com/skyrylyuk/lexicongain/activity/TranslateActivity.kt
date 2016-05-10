package com.skyrylyuk.lexicongain.activity

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.model.TokenPairRepository
import com.skyrylyuk.lexicongain.util.YandexTranslate
import org.jetbrains.anko.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */

class TranslateActivity : Activity(), AnkoLogger {

    @Inject
    lateinit var repository: TokenPairRepository

    @Inject
    lateinit var service: YandexTranslate

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
                visibility = View.INVISIBLE
            }.lparams(width = matchParent, height = wrapContent)
            layoutTransition = LayoutTransition()
            onClick {
                finish()
            }
        }

        if (intent.action == Intent.ACTION_SEND) {
            val original = intent.getStringExtra(Intent.EXTRA_TEXT).trim()
            txvOriginalText.text = original
            txvTranslateText.text = original

            val mainThread = AndroidSchedulers.mainThread()

            service.detect(text = original)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .map { response ->
                        response?.get("lang")?.asString
                    }
                    .flatMap { originalLang ->
                        service.translate(lang = "$originalLang-uk", text = original)
                    }
                    .map { response ->
                        response.get(YandexTranslate.TEXT).asString
                    }.observeOn(mainThread)
                    .doOnNext {
                        txvTranslateText.text = it
                        txvTranslateText.visibility = View.VISIBLE
                    }
                    .doOnNext { translation ->
                        val tokenPair = TokenPair().apply {
                            originalText = original
                            translateText = translation
                        }

                        repository.add(tokenPair)
                    }
                    .delay(4, TimeUnit.SECONDS)
                    .observeOn(mainThread)
                    .subscribe({
                        txvTranslateText.visibility = View.GONE
                        finish()
                        //TODO fix slide up out animation R.anim.anim_out_up
                        this.overridePendingTransition(R.anim.anim_empty, android.R.anim.fade_out)
                    }, errorHandler)
        }

    }

    val errorHandler = fun(throwable: Throwable) {
        txvOriginalText.text = "WTF"
        error { " Request to server finish with error: ${throwable.message} " }

        //  txvTranslateText.visibility = View.GONE
        finish()
        //TODO fix slide up out animation R.anim.anim_out_up
        overridePendingTransition(R.anim.anim_empty, android.R.anim.fade_out)
    }
}
