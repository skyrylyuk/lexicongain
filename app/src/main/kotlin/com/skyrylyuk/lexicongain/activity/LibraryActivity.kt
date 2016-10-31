package com.skyrylyuk.lexicongain.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.github.ajalt.timberkt.Timber.d
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.skyrylyuk.lexicongain.LexiconGainApplication
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.item_library.view.*
import javax.inject.Inject

/**
 *
 * Created by skyrylyuk on 11/17/15.
 */
class LibraryActivity : AppCompatActivity() {

    @Inject
    lateinit var ref: DatabaseReference

    lateinit var adapter: FirebaseRecyclerAdapter<TokenPair, TokenPairViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        LexiconGainApplication.graph.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val query: Query = ref.orderByChild("updateDate").limitToFirst(100)
        adapter = object : FirebaseRecyclerAdapter<TokenPair, TokenPairViewHolder>(TokenPair::class.java, R.layout.item_library, TokenPairViewHolder::class.java, query) {
            override fun populateViewHolder(viewHolder: TokenPairViewHolder?, model: TokenPair?, position: Int) {
                val postRef = getRef(position)
                d { "==> postRef = ${postRef}" }

                // Set click listener for the whole post view
                val postKey = postRef.key
                d { "==> postKey = ${postKey}" }
                d { "==> model = ${model}" }

                viewHolder?.bindTo(model)

                viewHolder?.root?.setBackgroundResource(if(position % 2 == 0) {R.color.main_color} else {R.color.slave_color})
            }
        }

        tokens.adapter = adapter
        tokens.layoutManager = LinearLayoutManager(this)


//        val tokenPairAdapter = TokenPairAdapter(this, repository.query())

        /* listView {
             id = android.R.id.list
             choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
             itemsCanFocus = false
 //            adapter = tokenPairAdapter
             onItemClick { adapterView, view, i, l ->
 //                val tokenPair = tokenPairAdapter.getItem(i)

 //                AddDialog.newInstance(tokenPair.originalText).show(fragmentManager, AddDialog.TAG)
             }
             setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
                 override fun onItemCheckedStateChanged(mode: ActionMode?, position: Int, id: Long, checked: Boolean) {
 //                    tokenPairAdapter.selectView(position, checked)

                     mode?.setTitle(R.string.action_library_edit)
 //                    mode?.subtitle = "Checked ${tokenPairAdapter.getSelectionItemCount()}"
                 }

                 override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                     return false
                 }

                 override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                     when (item?.itemId) {
                         R.id.action_delete -> {
 //                            val selectedIds: SparseBooleanArray = tokenPairAdapter.getSelectedIds()

                             // All writes must be wrapped in a transaction to facilitate safe multi threading
 //                            Observable.range(0, selectedIds.size())
 //                                    .filter { selectedIds.valueAt(it) }
 //                                    .map { tokenPairAdapter.getItem(selectedIds.keyAt(it)) }
 //                                    .filter { it != null }
 //                                    .subscribe { tokenPair ->
 //                                        repository.remove(TokenPairSpecification(tokenPair.originalText))
 //                                    }
 //
 //
 //                            mode?.finish()
 //                            tokenPairAdapter.removeSelection()
 //                            tokenPairAdapter.notifyDataSetChanged()
                             return true
                         }
                         else -> {
                             return false
                         }
                     }

                 }

                 override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                     supportActionBar?.setHomeActionContentDescription("ContentDescription")

                     mode?.menuInflater?.inflate(R.menu.library_action_mode_menu, menu)

                     return true
                 }

                 override fun onDestroyActionMode(mode: ActionMode?) {
                     println("onDestroyActionMode")
 //                    tokenPairAdapter.removeSelection()
                     supportActionBar?.setDisplayHomeAsUpEnabled(true)
                 }
             })
         }*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.library_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
                return true
            }
/*
            R.id.action_export -> {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file: File = File(dir, "lexicongain.backup")

                file.delete()

                realm.writeCopyTo(file)

                println("Complite")
            }
            R.id.action_import -> {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file: File = File(dir, "lexicongain.backup")

                if (file.exists()) {
                    realm.executeTransaction {
                        file.readLines().forEach {
                            val split = it.split(":")
                            println("it = $split")
                            realm.copyToRealm(TokenPair(
                                    originalText = split[0],
                                    translateText = split[1],
                                    phase = split[2].toInt()
                            ))
                        }

                    }
                }
            }
*/
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }

    /*
    class TokenPairAdapter(context: Context, val realmResults: RealmResults<TokenPair>) :
                RealmBaseAdapter<TokenPair>(context, realmResults) {

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

            fun selectView(position: Int, value: Boolean) {
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
        */
    class TokenPairViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        val original: TextView by lazy { root.original}
        val translate: TextView by lazy { root.translate }

        fun bindTo(tokenPair: TokenPair?) {
            tokenPair?.let {
                original.text = tokenPair.originalText
                translate.text = tokenPair.translateText
            }
        }
    }
}