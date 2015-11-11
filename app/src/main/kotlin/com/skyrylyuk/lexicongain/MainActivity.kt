package com.skyrylyuk.lexicongain

//import android.support.design.widget.Snackbar
//import android.support.v7.app.AppCompatActivity

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.activity_main.addButton

/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
//class MainActivity : AppCompatActivity() {
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        addButton.setOnClickListener {
            //            Snackbar.make(content, "Add", Snackbar.LENGTH_SHORT).show()
        }
    }
}
