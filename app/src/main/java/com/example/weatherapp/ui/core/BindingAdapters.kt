package com.example.weatherapp.ui.core

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.weatherapp.ui.main.MainScreenViewModel.Status
import com.squareup.picasso.Picasso
@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Status) {
    view.visibility =
        when (visible) {
            Status.Success, Status.Loading -> View.VISIBLE
            Status.Error -> View.INVISIBLE
        }
}
@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("android:imageUrl", "android:imageDefault")
fun loadImage(imageView: ImageView, url: String, placeholder: Drawable) {
    if (url == null) {
        imageView.setImageDrawable(placeholder)
    } else {
        Picasso.get()
            .load(url)
            .into(imageView)
    }
}
