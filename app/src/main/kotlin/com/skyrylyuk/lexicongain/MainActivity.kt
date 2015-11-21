package com.skyrylyuk.lexicongain

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.LinearLayout
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import kotlinx.android.synthetic.activity_main.*
import org.jetbrains.anko.startActivity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class MainActivity : AppCompatActivity() {

    val SENSITIVE: Int = 10
    val THRESHOLD = 15

    private val TIME_SHIFT_PERIOD = TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS)

    var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Open the default realm for the UI thread.
        realm = Realm.getDefaultInstance()

        showOldestCard()

        addButton.setOnClickListener {
            println("addButton.setOnClickListener")

            AddDialog.newInstance().show(fragmentManager, AddDialog.TAG)
        }

        txvOriginalText.setOnClickListener {

            txvTranslateText.visibility = View.VISIBLE
            val animation = ExpandAnimation(txvTranslateText)
            animation.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            animation.interpolator = DecelerateInterpolator()
            txvTranslateText.startAnimation(animation)
        }

        val color = resources.getColor(R.color.slave_color)
        var startX: Float = 0f
        val hsv = FloatArray(3)
        var shift: Int

        txvTranslateText.setOnTouchListener { view, motionEvent: MotionEvent ->
            val actionMasked = motionEvent.actionMasked

            when (actionMasked) {
                ACTION_DOWN -> {
                    startX = motionEvent.x
                }
                ACTION_MOVE -> {
                    Color.colorToHSV(color, hsv)

                    shift = ((startX - motionEvent.x) / SENSITIVE).toInt()
                    hsv[0] += shift

                    view.setBackgroundColor(Color.HSVToColor(hsv))
                }

                ACTION_UP, ACTION_OUTSIDE -> {
                    when {
                        shift > THRESHOLD -> {
                            markOldestCard(1)
                        }
                        shift < -THRESHOLD -> {
                            markOldestCard(3)
                        }
                        else -> {
                            //todo  add default action or implement null operation
                            println("default")
                        }
                    }

                    val animation = CollapseAnimation(txvTranslateText)
                    animation.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                    animation.interpolator = AnticipateInterpolator()
                    animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {
                            throw UnsupportedOperationException()
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            showOldestCard()
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }
                    })
                    txvTranslateText.startAnimation(animation)
                }
            }

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_library) {
            startActivity<LibraryActivity>()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    fun markOldestCard(date: Int) {
        val allObjects = realm.allObjects(TokenPair::class.java)
        allObjects.sort("updateDate")

        if (allObjects.size > 0) {
            val tokenPair = allObjects.first()
            realm.executeTransaction {
                if (date != 0)
                    tokenPair.updateDate = Date(tokenPair.updateDate.time + TIME_SHIFT_PERIOD)
            }
        }
    }

    fun showOldestCard() {
        txvTranslateText.visibility = View.GONE
        txvTranslateText.setBackgroundResource(R.color.slave_color)

        val allObjects = realm.allObjects(TokenPair::class.java)
        allObjects.sort("updateDate")

        if (allObjects.size != 0) {
            val findFirst = allObjects.first()
            txvOriginalText.text = findFirst?.originalText ?: getString(R.string.original_text)
            txvTranslateText.text = findFirst?.translateText ?: getString(R.string.translate_text)
        }
    }

    class ExpandAnimation(internal val view: View) : Animation() {

        var lp: LinearLayout.LayoutParams? = null

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)

            lp = view.layoutParams as LinearLayout.LayoutParams
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            lp?.weight = interpolatedTime
            view.layoutParams = lp;
        }

    }

    class CollapseAnimation(internal val view: View) : Animation() {

        var lp: LinearLayout.LayoutParams? = null

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)

            lp = view.layoutParams as LinearLayout.LayoutParams
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            lp?.weight = 1 - interpolatedTime
            view.layoutParams = lp;
        }
    }
}
