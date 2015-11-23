package com.skyrylyuk.lexicongain.util

import java.util.*

/**
 *
 * Created by skyrylyuk on 11/23/15.
 */


operator fun Date.plus(timeShift: Long): Date {
    this.time += timeShift

    return this
}
