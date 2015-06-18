package com.skyrylyuk.lexicongain

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import io.realm.Realm
import io.realm.RealmResults

//@CompileStatic
class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    private Realm realm

    @InjectView(R.id.txvText)
    TextView textView

    @InjectView(R.id.listView)
    ListView listView

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings)

        SwissKnife.inject this

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action == Intent.ACTION_SEND) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            textView.text = sharedText
        }

        realm = Realm.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart()

        def query = realm.where(TokenPair.class)
        query.maximumDate("translateDate")
        TokenPair tokenPair = query.findFirst()
        Log.i(TAG, "tokenPair = " + tokenPair?.translateText);
        Log.i(TAG, "tokenPair = " + tokenPair?.translateDate);

        RealmResults<TokenPair> tokenPairs = realm.where(TokenPair.class).findAll()
        tokenPairs.sort("translateDate", RealmResults.SORT_ORDER_DESCENDING)

        def adapter = new TokenPairAdapter(this, android.R.layout.simple_list_item_1, tokenPairs, false)
        listView.setAdapter(adapter)
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
