package com.skyrylyuk.lexicongain.presenter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.animation.Animation
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.view.CollapseAnimation
import com.skyrylyuk.lexicongain.view.ExpandAnimation
import org.jetbrains.anko.backgroundColor
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */

class TokenPairPresenter {

    @Inject
    lateinit var context: Context


    var slaveColor = 0

    var originalText: TextView? = null
    var translateText: TextView? = null

    init {
        LexiconGainApplication.graph.inject(this)
        slaveColor = ContextCompat.getColor(context, R.color.slave_color);
    }

    fun attachView(txvOriginalText: TextView, txvTranslateText: TextView) {
        originalText = txvOriginalText
        translateText = txvTranslateText
    }

    fun detachView() {
        originalText = null
        translateText = null
    }

    fun showNextCard() {
        translateText?.backgroundColor = slaveColor
        val animation = CollapseAnimation(translateText!!)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                throw UnsupportedOperationException()
            }

            override fun onAnimationEnd(animation: Animation?) {
//                val nextCard = repository.getNextCard()
//
//                originalText?.text = nextCard?.originalText ?: context.getString(R.string.original_text)
//                translateText?.text = nextCard?.translateText ?: context.getString(R.string.translate_text)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        translateText?.startAnimation(animation)

    }

    fun showTranslation() {
        translateText?.startAnimation(ExpandAnimation(translateText as TextView))
        translateText?.requestLayout()
        translateText?.requestLayout()
    }

    fun markOldestCard(date: Boolean) {

/*
        val tokenPair = repository.queryCopyFromRealm(ListTokenPairSpecification()).firstOrNull()
        if (tokenPair != null) {

            tokenPair.apply {

                if (date) {
                    phase++
                }

                updateDate += getPhaseDuration(phase)
            }

            repository.add(tokenPair)
        }
*/

        showNextCard()
    }

    fun getPhaseDuration(phase: Int): Long {
        val sqrt5 = Math.sqrt(5.0)
        val phi = (sqrt5 + 1) / 2


        Math.floor(Math.pow(phi, phase.toDouble()) / sqrt5 + 0.5).toInt()

        return TimeUnit.MILLISECONDS.convert(if (phase != 0) phase.toLong() else 1, TimeUnit.DAYS)
    }
}