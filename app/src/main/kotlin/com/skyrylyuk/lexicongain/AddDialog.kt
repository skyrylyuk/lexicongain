package com.skyrylyuk.lexicongain

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import com.jakewharton.rxbinding.widget.afterTextChangeEvents
import com.skyrylyuk.lexicongain.model.TokenPair
import io.realm.Realm
import org.jetbrains.anko.UI
import org.jetbrains.anko.editText
import org.jetbrains.anko.padding
import org.jetbrains.anko.verticalLayout
import retrofit.RestAdapter
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


/**
 *
 * Created by skyrylyuk on 11/20/15.
 */
class AddDialog : DialogFragment() {

    var original: String = ""
    var translation: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {

        var restAdapter = RestAdapter.Builder()
                .setEndpoint(YandexTranslate.HOST)
                .build()

        val service = restAdapter.create(YandexTranslate::class.java)

        val view = UI {
            verticalLayout {
                padding = 5
                val txvOriginal: EditText = editText {
                    hint = "Original text"
                }
                val txvTranslation: EditText = editText {
                    hint = "Translated text"
                }

                txvOriginal.afterTextChangeEvents()
                        .skip(1)
                        .debounce(650, TimeUnit.MILLISECONDS)
                        .doOnNext {
                            original = it.editable().toString()
                            service.translate(YandexTranslate.API_KEY, YandexTranslate.LANG, original)
                                    .map({ response -> response.get(YandexTranslate.TEXT).asString })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        translation = it
                                        txvTranslation.setText(translation)
                                    }
                        }
                        .subscribe()
            }
        }.toView()

        val dialog = AlertDialog.Builder(activity)
                .setView(view)
                .setPositiveButton("Ok", onPositiveFunction)
                .setNegativeButton("Cancel", null)
                .create()

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog.window.attributes.windowAnimations = R.style.DialogAnimation_Window
    }

    val onPositiveFunction: (DialogInterface, Int) -> Unit = { dialog, which ->
        // Open the default realm
        var realm = Realm.getDefaultInstance()

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.beginTransaction()

        val tokenPair = TokenPair().apply {
            originalText = original
            translateText = translation
        }

        realm.copyToRealmOrUpdate(tokenPair)

        // When the transaction is committed, all changes a synced to disk.
        realm.commitTransaction()
    }


    companion object {

        public val TAG: String = AddDialog::class.java.simpleName

        fun newInstance(): AddDialog {
            val args = Bundle()

            val fragment = AddDialog()
            fragment.arguments = args
            return fragment
        }
    }
}