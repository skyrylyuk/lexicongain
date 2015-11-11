package com.skyrylyuk.lexicongain

//import android.support.design.widget.Snackbar
//import android.support.v7.app.AppCompatActivity

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import kotlinx.android.synthetic.activity_main.addButton
import kotlinx.android.synthetic.activity_main.content
import kotlin.properties.Delegates

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
//class MainActivity : AppCompatActivity() {
class MainActivity : Activity() {

    private var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Open the default realm for the UI thread.
        realm = Realm.getInstance(this)


        addButton.setOnClickListener {

            val findFirst = realm.where(TokenPair::class.java).findFirst()

            Snackbar.make(content, "Add ${findFirst.originalText}", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()

    }
}
