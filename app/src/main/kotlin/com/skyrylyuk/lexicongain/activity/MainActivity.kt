package com.skyrylyuk.lexicongain.activity

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.Timber.d
import com.github.ajalt.timberkt.i
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPairHolder
import com.skyrylyuk.lexicongain.presenter.TokenPairPresenter
import com.skyrylyuk.lexicongain.util.parse
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import com.transitionseverywhere.TransitionSet
import com.transitionseverywhere.extra.Scale
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_card.view.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject


/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class MainActivity : AppCompatActivity(), TextWatcher {

    val SENSITIVE: Int = 10
    val THRESHOLD = 15

    @Inject
    lateinit var presenter: TokenPairPresenter

    @Inject
    lateinit var ref: DatabaseReference

    var startX: Float = 0f
    val hsvSource = FloatArray(3)
    var shift: Float = 0f

    var currentTokenHolder: TokenPairHolder? = null

    val slaveColor: Int by lazy { ContextCompat.getColor(this, R.color.slave_color) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LexiconGainApplication.graph.inject(this)

        toolbar.title = getString(R.string.app_name)


        txvOriginalText.setOnClickListener {

            toggleState()
        }

        addButton.setOnClickListener {
            val dialog = MaterialDialog.Builder(this)
                    .title(R.string.add_card_title)
                    .customView(R.layout.dialog_add_card, false)
                    .positiveText(R.string.add_card_agree)
                    .onPositive { dialog, which ->
                        val newOriginal = dialog.view.newOriginalText.text
                        val newTranslate = dialog.view.newTranslateText.text
                        i { "==> $newOriginal $newTranslate" }
                    }
                    .negativeText(R.string.add_card_disagree)
                    .show()


            dialog.view.newOriginalText.addTextChangedListener(this)

        }


        transitionsContainer.setOnTouchListener { view, motionEvent: MotionEvent ->
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
//                        else -> {
//                            txvTranslateText.setBackgroundColor(slaveColor)
//                        }
                    }
                }
            }

            true
        }

        ref.orderByChild("updateDate").limitToFirst(1).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {
                i { "==> ValueEventListener onCancelled: $error" }
            }

            override fun onDataChange(data: DataSnapshot?) {
                i { "==> ValueEventListener onDataChange $data" }
                currentTokenHolder = data?.parse()
                currentTokenHolder?.let {
                    val currentToken = it.value
                    txvOriginalText.text = currentToken.originalText
                    txvTranslateText.text = currentToken.translateText
                }
            }
        })

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)

        navigation.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_library -> {
                    startActivity<LibraryActivity>()
                    overridePendingTransition(R.anim.slide_in_rigth, android.R.anim.fade_out)
                }
                R.id.action_setting -> {
                    startActivity<SettingsActivity>()
                    overridePendingTransition(R.anim.slide_in_rigth, android.R.anim.fade_out)
                }
                else -> {
                    drawerLayout.closeDrawers()
                }
            }

            drawerLayout.closeDrawers()

            true
        }

        toggle.syncState()

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if ((clipboard.hasPrimaryClip())) {

            val item = clipboard.primaryClip.getItemAt(0)
            val pasteData = item.text

            d { "==> pasteData = $pasteData" }
            //TODO@skyrylyuk on 11/7/16 - implement add token from clipboard
//            Snackbar.make(container, "Clipboard contain text", Snackbar.LENGTH_LONG)
//                    .setAction("Add") { throw UnsupportedOperationException("not implemented") }
//                    .setActionTextColor(Color.parseColor("#00ff00"))
//                    .show()
        }

    }

    override fun onResume() {
        super.onResume()
        txvTranslateText.visibility = View.GONE
    }

    private fun toggleState() {
        TransitionManager.beginDelayedTransition(transitionsContainer, TransitionSet()
                .addTransition(Slide(Gravity.BOTTOM))
                .addTransition(Scale())
                .setDuration(350)
                .setInterpolator(FastOutSlowInInterpolator()))

        if (txvTranslateText.visibility == View.VISIBLE) {
            txvTranslateText.visibility = View.GONE
        } else {
            txvTranslateText.visibility = View.VISIBLE
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main_drawer, menu)
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
    }*/

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        d { "==> p0 = $p0" }
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()
    }

    fun markOldestCard(date: Boolean) {
        toggleState()
        transitionsContainer.setBackgroundColor(slaveColor)

        currentTokenHolder?.let {
            val value = it.value

            if (date) {
                value.phase++
            }

            value.updateDate += presenter.getPhaseDuration(value.phase)

            ref.child(it.key).setValue(value)
        }
    }
}
