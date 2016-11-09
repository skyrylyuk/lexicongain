package com.skyrylyuk.lexicongain.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.github.ajalt.timberkt.Timber.d
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.dialog_add_card.view.*
import kotlinx.android.synthetic.main.item_library.view.*
import javax.inject.Inject

/**
 *
 * Created by skyrylyuk on 11/17/15.
 */
open class LibraryActivity : AppCompatActivity() {

    @Inject
    lateinit var ref: DatabaseReference

    lateinit var adapter: FirebaseRecyclerAdapter<TokenPair, TokenPairViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        LexiconGainApplication.graph.inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val query: Query = ref.orderByChild("updateDate").limitToFirst(100)
        adapter = object : FirebaseRecyclerAdapter<TokenPair, TokenPairViewHolder>(TokenPair::class.java, R.layout.item_library, TokenPairViewHolder::class.java, query) {
            override fun populateViewHolder(viewHolder: TokenPairViewHolder?, model: TokenPair?, position: Int) {
                val postRef = getRef(position)
                d { "==> postRef = $postRef" }

                // Set click listener for the whole post view
                val tokenKey = postRef.key
                viewHolder?.root?.setOnClickListener {
                    showEditDialog(tokenKey, model)
                }
                d { "==> model = $model" }

                viewHolder?.bindTo(model)

                viewHolder?.root?.setBackgroundResource(if (position % 2 == 0) {
                    R.color.main_color
                } else {
                    R.color.slave_color
                })
            }
        }

        tokens.adapter = adapter
        tokens.layoutManager = LinearLayoutManager(this)
        tokens.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0  && addButton.isShown) {
                    addButton.hide()
                } else {
                    addButton.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        addButton.setOnClickListener {
            Snackbar.make(addButton, "Show", Snackbar.LENGTH_LONG).show()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater?.inflate(R.menu.library_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
                return true
            }
            R.id.action_export -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            // user is now signed out
                            startActivity(Intent(this@LibraryActivity, SplashActivity::class.java))
                            finish()
                        }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.cleanup()
    }

    fun showEditDialog(key: String?, model: TokenPair?) {
        val dialog = MaterialDialog.Builder(this)
                .title(R.string.add_card_title)
                .customView(R.layout.dialog_add_card, false)
                .positiveText(R.string.add_card_agree)
                .onPositive { dialog, which ->
                    model?.originalText = dialog.view.newOriginalText.text.toString()
                    model?.translateText = dialog.view.newTranslateText.text.toString()

                    saveModel(key, model)
                }
                .negativeText(R.string.add_card_disagree)
                .onNeutral { dialog, which ->  }
                .show()

        dialog.view.newOriginalText.setText(model?.originalText)
        dialog.view.newTranslateText.setText(model?.translateText)
    }

    fun takeModel(key: String?) {

    }

    fun saveModel(key: String?, model: TokenPair?) {
        ref.child(key).setValue(model)
    }

    class TokenPairViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        val original: TextView by lazy { root.original }
        val translate: TextView by lazy { root.translate }

        fun bindTo(tokenPair: TokenPair?) {
            tokenPair?.let {
                original.text = tokenPair.originalText
                translate.text = tokenPair.translateText
            }
        }
    }
}