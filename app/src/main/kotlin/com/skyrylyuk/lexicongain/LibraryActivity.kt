package com.skyrylyuk.lexicongain

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.SparseBooleanArray
import android.view.*
import android.widget.AbsListView
import android.widget.ListView
import android.widget.TextView
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import io.realm.RealmBaseAdapter
import io.realm.RealmResults
import org.jetbrains.anko.listView
import org.jetbrains.anko.onItemClick

/**
 *
 * Created by skyrylyuk on 11/17/15.
 */
class LibraryActivity : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()

    private val realmResults = realm.where(TokenPair::class.java).findAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar.setDisplayHomeAsUpEnabled(true)


        listView {
            choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
            itemsCanFocus = false
            val tokenPairAdapter = TokenPairAdapter(context, realmResults, true)
            adapter = tokenPairAdapter
            onItemClick { adapterView, view, i, l ->
                println("i = $i l = $l")
            }
            setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
                override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
                    tokenPairAdapter.selectView(position, checked)

                    mode?.setTitle(R.string.action_library_edit)
                    mode?.subtitle = "Checked ${tokenPairAdapter.getSelectionItemCount()}"
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.action_delete -> {
                            val selectedIds: SparseBooleanArray = tokenPairAdapter.getSelectedIds()

                            // Open the default realm
                            var realm = Realm.getDefaultInstance()

                            // All writes must be wrapped in a transaction to facilitate safe multi threading
                            realm.beginTransaction()

                            val size = selectedIds.size()
                            println("**************************************")
                            println("size = ${size}")
                            for (i in 0..size) {
                                if (selectedIds.valueAt(i)) {
                                    val tokenPair = tokenPairAdapter.getItem(selectedIds.keyAt(i))
                                    print("tokenPair = ${tokenPair.originalText}")
                                    realm.where(TokenPair::class.java).equalTo("originalText", tokenPair.originalText).findAll().removeLast()
                                    //                                    val isRemove = realmResults.removeF(tokenPair)
                                    //                                    println("isRemove = $isRemove")
                                }
                            }


                            // When the transaction is committed, all changes a synced to disk.
                            realm.commitTransaction()

                            mode?.finish()
                            tokenPairAdapter.removeSelection()
                            tokenPairAdapter.notifyDataSetChanged()
                            return true
                        }
                        else -> {
                            return false
                        }
                    }

                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    supportActionBar.setHomeActionContentDescription("ContentDescription")
                    println("onCreateActionMode")
                    mode?.menuInflater?.inflate(R.menu.library_main, menu)

                    return true
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    println("onDestroyActionMode")
                    tokenPairAdapter.removeSelection()
                    supportActionBar.setDisplayHomeAsUpEnabled(true)
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
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

        val mSelectedItemsIds = SparseBooleanArray();

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val inflate = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)

            val text1: TextView = inflate.findViewById(android.R.id.text1) as TextView
            val text2: TextView = inflate.findViewById(android.R.id.text2) as TextView

            val tokenPair = realmResults[position]
            text1.text = tokenPair.originalText
            text2.text = tokenPair.translateText

            if (mSelectedItemsIds.get(position, false)) {
                inflate.setBackgroundResource(R.color.main_color)
            } else {
                inflate.setBackgroundColor(Color.TRANSPARENT)
            }


            return inflate
        }

        public fun selectView(position: Int, value: Boolean) {
            if (value) {
                mSelectedItemsIds.put(position, value)
            } else {
                mSelectedItemsIds.delete(position)
            }

            notifyDataSetChanged();
        }

        fun removeSelection() {
            mSelectedItemsIds.clear() //= SparseBooleanArray()
            notifyDataSetChanged()
        }

        fun getSelectionItemCount(): Int {
            return mSelectedItemsIds.size()
        }

        fun getSelectedIds(): SparseBooleanArray {
            return mSelectedItemsIds;
        }
    }

    //    companion object {
    //        public val TAG:String = LibraryActivity::class.java.simpleName
    //    }
}