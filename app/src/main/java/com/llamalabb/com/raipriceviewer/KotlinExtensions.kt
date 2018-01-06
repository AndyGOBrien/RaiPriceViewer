package com.llamalabb.com.raipriceviewer

import java.text.NumberFormat

/**
 * Created by andyg on 1/4/2018.
 */


fun Double.toTwoDecimalPlacesAbs() = "%.2f".format(Math.abs(this))
fun Double.toTwoDecimalPlaces() = "%.2f".format(this)
fun String.formatNumber() : String = NumberFormat.getIntegerInstance().format(this.toDouble())