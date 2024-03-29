package com.example.weatherapp.ui.core

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
class RecyclerItemClickListener(
    context: Context?,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) :
    RecyclerView.OnItemTouchListener {
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    var mGestureDetector: GestureDetector
    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    init {
        mGestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }
            }
        )
    }
}
