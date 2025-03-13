package com.codetech.texttoimage.util

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

object Extension {
    fun Activity.showMessage(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    fun loadImage(
        imageView: ImageView,
        imageUrl: Bitmap,
        progressBar: ProgressBar,
        textView: TextView,
        view: View
    ) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .into(imageView)
        progressBar.gone()
        textView.visible()
        view.visible()
    }

    fun View.visible(){
        this.visibility = View.VISIBLE
    }

    fun View.gone(){
        this.visibility = View.GONE
    }
}