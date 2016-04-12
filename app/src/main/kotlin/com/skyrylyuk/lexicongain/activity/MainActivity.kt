package com.skyrylyuk.lexicongain.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.view.MotionEvent.*
import android.view.animation.Animation
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.presenter.IrregularVerbPresenter
import com.skyrylyuk.lexicongain.util.plus
import com.skyrylyuk.lexicongain.view.CollapseAnimation
import com.skyrylyuk.lexicongain.view.ExpandAnimation
import io.realm.Realm
import org.jetbrains.anko.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class MainActivity : AppCompatActivity(), AnkoLogger {

    val SENSITIVE: Int = 10
    val THRESHOLD = 15

    @Inject
    lateinit var irregularVerbPresenter: IrregularVerbPresenter

    var realm: Realm by Delegates.notNull()

    var txvOriginalText: TextView by Delegates.notNull()
    var txvTranslateText: TextView by Delegates.notNull()

    var startX: Float = 0f
    val hsvSource = FloatArray(3)
    var shift: Float = 0f

    val TAG = MainActivity::class.java.simpleName



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LexiconGainApplication.graph.inject(this)

//        info("London is the capital of Great Britain")

        val mainColor = ContextCompat.getColor(this, R.color.main_color)
        val slaveColor = ContextCompat.getColor(this, R.color.slave_color);
        verticalLayout {
            txvOriginalText = textView {
                backgroundColor = mainColor
                textColor = Color.BLACK
                gravity = Gravity.CENTER
                onClick {
                    txvTranslateText.visibility = View.VISIBLE
                    txvTranslateText.startAnimation(ExpandAnimation(txvTranslateText))
                    txvTranslateText.backgroundColor = slaveColor
                }
            }.lparams(width = matchParent, height = 0, weight = 1f)

            txvTranslateText = textView {
                textColor = Color.BLACK
                gravity = Gravity.CENTER
                onTouch { view, motionEvent: MotionEvent ->
                    val actionMasked = motionEvent.actionMasked

                    when (actionMasked) {
                        ACTION_DOWN -> {
                            startX = motionEvent.x
                        }
                        ACTION_MOVE -> {
                            Color.colorToHSV(slaveColor, hsvSource)

                            shift = (startX - motionEvent.x) / SENSITIVE
                            hsvSource[0] += shift

                            view.setBackgroundColor(Color.HSVToColor(hsvSource))
                        }

                        ACTION_UP, ACTION_OUTSIDE -> {
                            when {
                                shift > THRESHOLD -> {
                                    markOldestCard(true)
                                }
                                shift < -THRESHOLD -> {
                                    markOldestCard(false)
                                }
                                else -> {
                                    //todo  add default action or implement null operation
                                    println("default")
                                }
                            }

                            val animation = CollapseAnimation(txvTranslateText)
                            animation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) {
                                    throw UnsupportedOperationException()
                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                    irregularVerbPresenter.showOldestCard()
                                }

                                override fun onAnimationStart(animation: Animation?) {
                                }
                            })
                            txvTranslateText.startAnimation(animation)
                        }
                    }

                    true
                }
            }.lparams(width = matchParent, height = 0, weight = 1f)
        }

        irregularVerbPresenter.attachView(txvOriginalText, txvTranslateText)
        // Open the default realm for the UI thread.
        realm = Realm.getDefaultInstance()

        irregularVerbPresenter.showOldestCard()
        /*
                addButton.setOnClickListener {
                    println("addButton.setOnClickListener")
                    AddDialog.newInstance().show(fragmentManager, AddDialog.TAG)
                }

                txvOriginalText.setOnClickListener {
                }
        */
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_library -> {
                startActivity<LibraryActivity>()
            }
            R.id.action_irregular_verbs -> {
                //                startActivity<IrregularVerbsActivity>()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    fun markOldestCard(date: Boolean) {

        if (realm.where(TokenPair::class.java).count() > 0) {

            val tokenPair = realm.where(TokenPair::class.java).findAllSorted("updateDate").first()

            realm.executeTransaction {
                realm.copyToRealmOrUpdate(tokenPair.apply {

                    if (date) {
                        phase++
                    }

                    updateDate += getPhaseDuration(phase)
                })
            }
        }
    }

    fun getPhaseDuration(phase: Int): Long {
        val sqrt5 = Math.sqrt(5.0)
        val phi = (sqrt5 + 1) / 2

        Math.floor(Math.pow(phi, phase.toDouble()) / sqrt5 + 0.5).toInt()

        return TimeUnit.MILLISECONDS.convert(if (phase != 0) phase.toLong() else 1, TimeUnit.DAYS)
    }

/*
    fun showOldestCard() {
        txvTranslateText.visibility = View.GONE
        txvTranslateText.setBackgroundResource(R.color.slave_color)

        val findFirst = realm.where(TokenPair::class.java).findAllSorted("updateDate").firstOrNull()

        txvOriginalText.text = findFirst?.originalText ?: getString(R.string.original_text)
        txvTranslateText.text = findFirst?.translateText ?: getString(R.string.translate_text)
    }
*/
}
