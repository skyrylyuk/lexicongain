package com.skyrylyuk.lexicongain.presenter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.IrregularVerbRepository
import javax.inject.Inject

/**
 * Person project
 * Created by skyrylyuk on 4/7/16.
 */

class IrregularVerbPresenter {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var repository: IrregularVerbRepository

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

    fun showOldestCard() {
        translateText?.visibility = View.GONE
        translateText?.setBackgroundResource(R.color.slave_color)

        val findFirst = repository.getNextCard()

        originalText?.text = findFirst?.originalText ?: context.getString(R.string.original_text)
        translateText?.text = findFirst?.translateText ?: context.getString(R.string.translate_text)
    }

}