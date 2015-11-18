package com.skyrylyuk.lexicongain

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import org.jetbrains.anko.listView

/**
 *
 * Created by skyrylyuk on 11/17/15.
 */
class LibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar.setDisplayHomeAsUpEnabled(true)

        val realm = Realm.getDefaultInstance()
        val realmResults = realm.where(TokenPair::class.java).findAll()

        listView {
            adapter = TokenPairAdapter(context, realmResults, true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> {
                //                NavUtils.navigateUpFromSameTask(this)
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    class TokenPairAdapter(context: Context, realmResults: RealmResults<TokenPair>, automaticUpdate: Boolean) :
            RealmBaseAdapter<TokenPair>(context, realmResults, automaticUpdate) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val inflate = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)

            val text1: TextView = inflate.findViewById(android.R.id.text1) as TextView
            val text2: TextView = inflate.findViewById(android.R.id.text2) as TextView

            val tokenPair = realmResults[position]
            text1.text = tokenPair.originalText
            text2.text = tokenPair.translateText


            return inflate
        }
    }
}