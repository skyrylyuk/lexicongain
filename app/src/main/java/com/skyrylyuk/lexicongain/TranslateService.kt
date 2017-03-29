package com.skyrylyuk.lexicongain

import com.evernote.android.job.Job
import com.github.ajalt.timberkt.Timber.d
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject


class TranslateService : Job() {

    init {
        LexiconGainApplication.graph.inject(this)
    }

    @Inject
    lateinit var ref: DatabaseReference

    override fun onRunJob(params: Params?): Result {
        d { "==> onRunJob TAG = $TAG " }

        ref.database.app

        return Result.SUCCESS
    }


    companion object {

        const val TAG = "TranslateService"

        fun scheduleJob() {
/*
            JobRequest.Builder(TranslateService.TAG)
                    .setExecutionWindow(3000L, 4000L)
                    .setPeriodic(5000L)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
                    .setRequiresCharging(true)
                    .setRequiresDeviceIdle(true)
                    .build()
                    .schedule()
*/
        }
    }
}
