package com.skyrylyuk.lexicongain

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import kotlinx.android.synthetic.activity_main.addButton
import kotlinx.android.synthetic.activity_main.content
import kotlinx.android.synthetic.activity_main.txvOriginalText
import kotlinx.android.synthetic.activity_main.txvTranslateText
import kotlin.properties.Delegates

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class MainActivity : AppCompatActivity() {

    val SENSITIVE: Int = 10
    val THRESHOLD = 25

    var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Open the default realm for the UI thread.
        realm = Realm.getDefaultInstance()

        val count = realm.where(TokenPair::class.java).count()

        txvOriginalText.text = "${txvOriginalText.text} $count"

        addButton.setOnClickListener {

            val findFirst = realm.where(TokenPair::class.java)?.findFirst()

            Snackbar.make(content, "Add ${findFirst?.originalText}", Snackbar.LENGTH_SHORT).show()
        }

        txvOriginalText.setOnClickListener {
            if (txvTranslateText.visibility == View.VISIBLE) {
                txvTranslateText.visibility = View.GONE
            } else {
                txvTranslateText.visibility = View.VISIBLE
            }
        }


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
                    Color.colorToHSV(resources.getColor(R.color.slave_color), hsv)

                    shift = ((startX - motionEvent.x) / SENSITIVE).toInt()

                    view.setBackgroundColor(Color.HSVToColor(hsv))
                }
                ACTION_UP, ACTION_OUTSIDE -> {
                    view.setBackgroundResource(R.color.slave_color)
                    when {
                        shift > THRESHOLD -> {
                            println("correct")
                        }
                        shift < -THRESHOLD -> {
                            println("wrong")
                        }
                        else -> {
                            println("default")
                        }
                    }
                }
            }


            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }
}
