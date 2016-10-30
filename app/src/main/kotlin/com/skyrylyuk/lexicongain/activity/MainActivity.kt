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
import com.github.ajalt.timberkt.i
import com.google.firebase.database.*
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.model.TokenPairHolder
import com.skyrylyuk.lexicongain.presenter.TokenPairPresenter
import com.skyrylyuk.lexicongain.util.parse
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import java.text.SimpleDateFormat
import java.util.*
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

    @Inject
    lateinit var ref: DatabaseReference

    var txvOriginalText: TextView by Delegates.notNull()
    var txvTranslateText: TextView by Delegates.notNull()

    var startX: Float = 0f
    val hsvSource = FloatArray(3)
    var shift: Float = 0f

    var currentTokenHolder: TokenPairHolder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LexiconGainApplication.graph.inject(this)


        val mainColor = ContextCompat.getColor(this, R.color.main_color)
        val slaveColor = ContextCompat.getColor(this, R.color.slave_color)

        coordinatorLayout {
            verticalLayout {
                txvOriginalText = textView {
                    backgroundColor = mainColor
                    textColor = Color.BLACK
                    textSize = resources.getDimension(R.dimen.main_activity_font_size)
                    gravity = Gravity.CENTER
                    onClick {
                        val lp = txvTranslateText.layoutParams as LinearLayout.LayoutParams

                        if (lp.weight == 1.0f) {
                            presenter.showNextCard()
                        } else {
                            presenter.showTranslation()
                        }
                    }
                }.lparams(width = matchParent, height = 0, weight = 1f)

                txvTranslateText = textView {
                    textColor = Color.BLACK
                    textSize = resources.getDimension(R.dimen.main_activity_font_size)
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
                                        txvTranslateText.backgroundColor = slaveColor
                                    }
                                }
                            }
                        }

                        true
                    }
                }.lparams(width = matchParent, height = 0, weight = 1f)
            }.lparams(width = matchParent, height = matchParent)
            floatingActionButton {
                imageResource = R.drawable.ic_add_white
                onClick {


                            currentTokenHolder?.let {
                                val value = it.value

                                value.phase = 3
                                value.updateDate = System.currentTimeMillis()

                                ref.child(it.key).setValue(value)
//                                ref.child(tokenPairHolder.key).child("phase").setValue(2)
                            }

                    val timestamp = SimpleDateFormat("hh_mm", Locale.US).format(Date())

                    val value = TokenPair().apply {
                        originalText = "one $timestamp"
                        translateText = "one $timestamp"
                    }
//                    ref.push().setValue(value)
                }
                backgroundTintList = ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(ContextCompat.getColor(this@MainActivity, R.color.fab_color)))
            }.lparams {
                gravity = Gravity.BOTTOM or Gravity.RIGHT or Gravity.END
                margin = 10
            }
        }


        presenter.attachView(txvOriginalText, txvTranslateText)

        presenter.showNextCard()

        ref.orderByChild("updateDate").limitToFirst(    1).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                i { "==> ValueEventListener onCancelled" }
            }

            override fun onDataChange(p0: DataSnapshot?) {
                i { "==> ValueEventListener onDataChange $p0" }
                currentTokenHolder = p0?.parse()
                currentTokenHolder?.let {

                    val currentToken = it.value
                    txvOriginalText.text = currentToken.originalText
                    txvTranslateText.text = currentToken.translateText
                }
            }
        })

        ref.orderByChild("updateDate").limitToFirst(1).addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                i { "==> onChildMoved }" }
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                i { "==> onChildChanged" }
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                i { "==> onChildAdded" }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                i { "==> onChildRemoved" }
            }

            override fun onCancelled(p0: DatabaseError?) {
                i { "==> onCancelled" }
            }
        })
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
                overridePendingTransition(R.anim.slide_in_rigth, android.R.anim.fade_out)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()


    }
}
