package com.skyrylyuk.lexicongain.activity

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import com.jakewharton.rxbinding.widget.afterTextChangeEvents
import com.skyrylyuk.lexicongain.R
import com.skyrylyuk.lexicongain.model.TokenPair
import com.skyrylyuk.lexicongain.util.YandexTranslate
import io.realm.Realm
import org.jetbrains.anko.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 *
 * Created by skyrylyuk on 11/20/15.
 */
class AddDialog : DialogFragment() {

    // Open the default realm
    var realm = Realm.getDefaultInstance()

    @Inject
    lateinit var service: YandexTranslate


    var original: String = ""
    var translation: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {


        val key: String = arguments.getString(KEY, "")

        if (key.isNotEmpty()) {
            val tokenPair = realm.where(TokenPair::class.java).equalTo("originalText", key).findFirst()
            original = tokenPair.originalText
            translation = tokenPair.translateText
        }


        val view = UI {
            verticalLayout {
                padding = 5
                val txvOriginal: EditText = editText {
                    hint = "Original text"
                    setText(original)
                }.lparams(width = matchParent, height = wrapContent)

                val txvTranslation: EditText = editText {
                    hint = "Translated text"
                    setText(translation)
                }
                txvOriginal.afterTextChangeEvents()
                        .skip(1)
                        .debounce(650, TimeUnit.MILLISECONDS)
                        .doOnNext {
                            original = it.editable().toString()
                            service.translate(YandexTranslate.API_KEY, YandexTranslate.LANG, original)
                                    .map({ response -> response.get(YandexTranslate.TEXT).asString })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnError {
                                        println("Error!!!")
                                    }
                                    .subscribeOn(Schedulers.io())
                                    .subscribe {
                                        translation = it
                                        txvTranslation.setText(translation)
                                    }
                        }
                        .doOnError {
                            println("it = $it")
                        }
                        .subscribe()

                txvTranslation.afterTextChangeEvents()
                        .skip(1)
                        .debounce(650, TimeUnit.MILLISECONDS)
                        .doOnNext {
                            translation = it.editable().toString()
                        }
                        .subscribe()
            }
        }.view

        val dialog = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
                .setView(view)
                .setPositiveButton("Ok", onPositiveFunction)
                .setNegativeButton("Cancel", null)
                .create()

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments.getString(KEY, "").isEmpty()) {
            dialog.window.attributes.windowAnimations = R.style.DialogAnimation_Window
        }
    }


    override fun onStart() {
        super.onStart()

        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    val onPositiveFunction: (DialogInterface, Int) -> Unit = { dialog, which ->

        if (!original.isEmpty()) {

            // All writes must be wrapped in a transaction to facilitate safe multi threading
            realm.beginTransaction()

            val key: String = arguments.getString(KEY, "")

            val tokenPair = if (key.isNotEmpty()) {
                realm.where(TokenPair::class.java).equalTo("originalText", key).findFirst()
            } else {
                TokenPair()
            }


            tokenPair.apply {
                originalText = original
                translateText = translation
            }

            realm.copyToRealmOrUpdate(tokenPair)

            // When the transaction is committed, all changes a synced to disk.
            realm.commitTransaction()
        }
    }


    companion object {

        val TAG: String = AddDialog::class.java.simpleName
        val KEY: String = "TOKEN_PAIR_KEY"

        fun newInstance(tokenPairKey: String = ""): AddDialog {
            val args = Bundle()

            if (tokenPairKey.isNotEmpty()) {
                args.putString(KEY, tokenPairKey)
            }

            val fragment = AddDialog()
            fragment.arguments = args
            return fragment
        }
    }
}