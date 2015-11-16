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
import java.util.*
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

        showNextCard()

        addButton.setOnClickListener {

            val count = realm.where(TokenPair::class.java).count()

            Snackbar.make(content, "Add $count", Snackbar.LENGTH_SHORT).show()
        }

        txvOriginalText.setOnClickListener {
            if (txvTranslateText.visibility == View.VISIBLE) {
                txvTranslateText.visibility = View.GONE
            } else {
                txvTranslateText.visibility = View.VISIBLE
            }
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

                    val allObjects = realm.allObjects(TokenPair::class.java)
                    allObjects.sort("updateDate")

                    if (allObjects.size != 0) {

                        realm.executeTransaction {
                            val findFirst: TokenPair = allObjects.first()
                            findFirst.updateDate = Date()
                        }
                    }

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

                    showNextCard()

                }
            }


            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    fun showNextCard() {
        txvTranslateText.setBackgroundResource(R.color.slave_color)

        val allObjects = realm.allObjects(TokenPair::class.java)
        allObjects.sort("updateDate")

        if (allObjects.size != 0) {
            val findFirst = allObjects.first()
            txvOriginalText.text = findFirst?.originalText ?: getString(R.string.original_text)
            txvTranslateText.text = findFirst?.translateText ?: getString(R.string.translate_text)
            println("findFirst = $findFirst")
        }



    }
}
