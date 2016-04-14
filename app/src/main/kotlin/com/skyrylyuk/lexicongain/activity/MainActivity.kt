package com.skyrylyuk.lexicongain.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.widget.LinearLayout
import android.widget.TextView
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.presenter.TokenPairPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
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
    lateinit var presenter: TokenPairPresenter

    var txvOriginalText: TextView by Delegates.notNull()
    var txvTranslateText: TextView by Delegates.notNull()

    var startX: Float = 0f
    val hsvSource = FloatArray(3)
    var shift: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LexiconGainApplication.graph.inject(this)


        val mainColor = ContextCompat.getColor(this, R.color.main_color)
        val slaveColor = ContextCompat.getColor(this, R.color.slave_color);

        coordinatorLayout {
            verticalLayout {
                txvOriginalText = textView {
                    backgroundColor = mainColor
                    textColor = Color.BLACK
                    gravity = Gravity.CENTER
                    onClick {
                        var lp = txvTranslateText.layoutParams as LinearLayout.LayoutParams

                        if(lp.weight == 1.0f){
                            presenter.showNextCard()
                        } else {

                            presenter.showTranslation()
                        }
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
                                        presenter.markOldestCard(true)
                                    }
                                    shift < -THRESHOLD -> {
                                        presenter.markOldestCard(false)
                                    }
                                    else -> {
                                        //todo  add default action or implement null operation
                                        info("default")
                                    }
                                }

                                presenter.showNextCard()
                            }
                        }

                        true
                    }
                }.lparams(width = matchParent, height = 0, weight = 1f)
            }.lparams(width = matchParent, height = matchParent)
            floatingActionButton {
                imageResource = R.drawable.ic_add_white
                onClick {
                    AddDialog.newInstance().show(fragmentManager, AddDialog.TAG)
                }
                backgroundTintList = ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(resources.getColor(R.color.fab_color)))
            }.lparams{
                gravity = Gravity.BOTTOM or Gravity.RIGHT
                margin = 10
            }
        }


        presenter.attachView(txvOriginalText, txvTranslateText)

        presenter.showNextCard()
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
                overridePendingTransition(R.anim.slide_in_rigth, android.R.anim.fade_out);
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()
    }
}
