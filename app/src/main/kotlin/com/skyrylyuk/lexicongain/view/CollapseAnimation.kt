package com.skyrylyuk.lexicongain.view

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnticipateInterpolator
import android.view.animation.Transformation
import android.widget.LinearLayout
import kotlin.properties.Delegates

/**
 * Person project
 * Created by skyrylyuk on 4/6/16.
 */
class CollapseAnimation(internal val view: View) : Animation() {

    var lp: LinearLayout.LayoutParams by Delegates.notNull()

    init {
        duration = view.context.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        interpolator = AnticipateInterpolator()

    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)

        lp = view.layoutParams as LinearLayout.LayoutParams
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        lp.weight = 1.0000001f - interpolatedTime
        view.layoutParams = lp
    }
}