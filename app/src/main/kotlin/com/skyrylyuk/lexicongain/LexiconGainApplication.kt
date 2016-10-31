package com.skyrylyuk.lexicongain

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.google.firebase.database.DatabaseReference
import timber.log.Timber


/**
 *
 * Created by skyrylyuk on 11/11/15.
 */
class LexiconGainApplication : Application() {

    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent

        @JvmStatic lateinit var ref: DatabaseReference
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {

        Timber.plant(Timber.DebugTree())


        graph = DaggerApplicationComponent.builder().androidModule(AndroidModule(this)).build()


/*
        firebase = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = firebase.reference

        Log.w("App", "==> reference  $reference")
        w{"==> reference  $reference"}



        val key = reference.child("lexicongain").key
        Log.w("App", "==> key  $key")

        reference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                e{"==> error: $error"}
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                val list = snapshot?.value as List<TokenPair>
                i{"==> snapshot raw $list"}
                i{"==> snapshot raw ${list.size}"}
//                i{"==> snapshot raw ${list.size}"}
            }
//            override fun onDataChange(DataSnapshot snapshot) {
//                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
//            }
//            @Override public void onCancelled(FirebaseError error) { }
        })

        reference.limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError?) {
                e{"==> error2: $error"}
            }

            override fun onDataChange(data: DataSnapshot?) {
                i{"==> snapshot raw2: ${data?.value}"}
//                i{"==> snapshot raw2: ${data?.getValue(TokenPair::class.java)}"}
            }
        })
*/

    }
}

