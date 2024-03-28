package com.knucklebones.functions

import android.view.View

fun isOverlapping(curr: IntArray, slot: IntArray, itt: Dice, view: View): Boolean {
    return ((curr[0] + view.width/2 > slot[0]) && (curr[0] + view.width/2 < slot[0] + itt.slot.width) &&
            (curr[1] + view.height/2 > slot[1]) && (curr[1] + view.height/2 < slot[1] + itt.slot.height))
}