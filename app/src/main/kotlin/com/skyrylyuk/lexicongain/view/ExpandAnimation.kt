package com.skyrylyuk.lexicongain.view

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.LinearLayout
import com.skyrylyuk.lexicongain.R
import kotlin.properties.Delegates

/**
 * Person project
 * Created by skyrylyuk on 4/6/16.
 */

class ExpandAnimation(internal val view: View) : Animation() {

    var lp: LinearLayout.LayoutParams by Delegates.notNull()

    init {
        duration = view.context.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        interpolator = DecelerateInterpolator()
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)

        lp = view.layoutParams as LinearLayout.LayoutParams
    }


    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        lp.weight = interpolatedTime
        view.layoutParams = lp
    }

}