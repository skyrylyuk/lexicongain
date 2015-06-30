package com.skyrylyuk.lexicongain

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import io.realm.Realm

//@CompileStatic
class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Realm realm

    @InjectView(R.id.txvOriginalText)
    TextView txvOriginalText

    @InjectView(R.id.txvTranslateText)
    TextView txvTranslateText

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)

        SwissKnife.inject this

        realm = Realm.getInstance(this);

        txvOriginalText.setOnClickListener {
            txvTranslateText.visibility = txvTranslateText.visibility == View.VISIBLE ? View.GONE : View.VISIBLE
        }

/*
        txvTranslateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            boolean onTouch(View v, MotionEvent event) {
                return false
            }
        })
*/
        txvTranslateText.setOnClickListener {
            txvOriginalText.text = ""
            txvTranslateText.visibility = View.GONE

            txvTranslateText.postDelayed({ markLastItemSuccess() }, 750)
        }
    }

    @Override
    protected void onStart() {
        super.onStart()

        showLastItem()
    }

    def markLastItemSuccess() {
        realm.beginTransaction();

        def query = realm.where(TokenPair.class)
        if (query.count()) {
            TokenPair tokenPair = query.findAllSorted("translateDate")?.first()

            tokenPair.translateDate = new Date()
        }

        realm.commitTransaction()

        showLastItem()
    }

    def showLastItem = {
        def query = realm.where(TokenPair.class)
        if (query.count()) {
            TokenPair tokenPair = query.findAllSorted("translateDate")?.first()

            txvOriginalText.text = tokenPair?.originalText
            txvTranslateText.text = tokenPair?.translateText
        }
    }

/*
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
            startActivity(new Intent(this, SettingsActivity.class))
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
}
