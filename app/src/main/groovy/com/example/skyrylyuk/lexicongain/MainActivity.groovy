package com.example.skyrylyuk.lexicongain

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem
import android.widget.TextView
import android.widget.*
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.*
import com.arasthel.swissknife.annotations.resources.StringRes
import com.arasthel.swissknife.dsl.components.GAsyncTask
import groovy.transform.CompileStatic;

//@CompileStatic
class MainActivity extends AppCompatActivity {

    @InjectView(R.id.txvText)
    TextView textView

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)

        SwissKnife.inject this

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        textView.setText(action + ' ' + type)

        if(action == Intent.ACTION_SEND){
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            textView.text = sharedText
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
