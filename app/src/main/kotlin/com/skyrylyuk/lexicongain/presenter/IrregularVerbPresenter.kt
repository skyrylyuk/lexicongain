package com.skyrylyuk.lexicongain.presenter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPairRepository
import com.skyrylyuk.lexicongain.view.ExpandAnimation
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource
import javax.inject.Inject

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */

class IrregularVerbPresenter {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var repository: TokenPairRepository

    var originalText: TextView? = null
    var translateText: TextView? = null

    init {
        LexiconGainApplication.graph.inject(this)
    }

    fun attachView(txvOriginalText: TextView, txvTranslateText: TextView) {
        originalText = txvOriginalText
        translateText = txvTranslateText
    }

    fun detachView() {
        originalText = null
        translateText = null
    }

    fun showTranslation(){
        translateText?.visibility = View.VISIBLE

        translateText?.startAnimation(ExpandAnimation(translateText as TextView))

        val slaveColor = ContextCompat.getColor(context, R.color.slave_color);
        translateText?.backgroundColor = slaveColor
    }

    fun showOldestCard() {
        translateText?.visibility = View.GONE
        translateText?.setBackgroundResource(R.color.slave_color)

        val findFirst = repository.getNextCard()

        originalText?.text = findFirst?.originalText ?: context.getString(R.string.original_text)
        translateText?.text = findFirst?.translateText ?: context.getString(R.string.translate_text)
    }

}