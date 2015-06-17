package com.skyrylyuk.lexicongain

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnUIThread
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import io.realm.Realm
import org.json.JSONObject

/**
 * Airbeem project
 * Created by skyrylyuk on 6/16/15.
 */
class TranslateActivity extends Activity {
    public static final String API_KEY = 'trnsl.1.1.20150616T094415Z.ab702c0fb8064c33.a4bb58f089d2c9167ce05c0078ffd982a25f982e'

    OkHttpClient client = new OkHttpClient();

    @InjectView(R.id.txvOriginal)
    TextView txvOriginal

    @InjectView(R.id.prbTranslate)
    ProgressBar prbTranslate

    @InjectView(R.id.txvTranslate)
    TextView txvTranslate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)

        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView R.layout.activity_translate

        SwissKnife.inject this

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == Intent.ACTION_SEND) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            txvOriginal.text = sharedText

            translateOnBackground sharedText
        }
    }

    @OnBackground()
    public void translateOnBackground(String originalText) {

        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=${API_KEY}&lang=en-ru&text=${originalText}"
        Request request = new Request.Builder()
                .url(url)
                .build()
        Response response = client.newCall(request).execute()
        String jsonText = response.body().string()

        JSONObject jsonObj = new JSONObject(jsonText);

        def code = jsonObj.getInt('code')
        if (code == 200) {
            def jSONArray = jsonObj.getJSONArray('text')

            def responseText = jSONArray?.values?.join(' ') as String
            showResultOnUIThread(responseText)

            save(originalText, responseText)

        } else {
            showResultOnUIThread("Server unavailable")
        }
    }

    @OnUIThread()
    public void showResultOnUIThread(String translatedText) {
        txvTranslate.visibility = View.VISIBLE
        txvTranslate.setText(translatedText)


        prbTranslate.setIndeterminate false

    }

    void save(String original, String translate){
        Context context = this
        Realm realm = Realm.getInstance(context)

        realm.beginTransaction()

        TokenPair tokenPair = realm.createObject(TokenPair.class)
        tokenPair.originalText = original
        tokenPair.translateText = translate

        tokenPair.translateDate = new Date()

        realm.commitTransaction()
    }
}
