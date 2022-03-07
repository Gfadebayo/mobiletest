package com.edvora.mobiletest.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.fetchAndDisplayImage(link: String){
    Glide.with(this)
            .load(link)
            .into(this)
            .request!!.apply {
                if(!isRunning) begin()
            }
}