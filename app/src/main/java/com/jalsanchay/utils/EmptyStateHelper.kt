package com.jalsanchay.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.jalsanchay.R

/**
 * Helps manage empty state UI across the app
 */
object EmptyStateHelper {
    
    fun showEmptyState(
        container: View,
        title: String,
        message: String,
        iconResId: Int = R.drawable.ic_launcher_foreground
    ) {
        container.visibility = View.VISIBLE
        
        val icon = container.findViewById<ImageView>(R.id.empty_state_icon)
        val titleView = container.findViewById<TextView>(R.id.empty_state_title)
        val messageView = container.findViewById<TextView>(R.id.empty_state_message)
        
        icon.setImageResource(iconResId)
        titleView.text = title
        messageView.text = message
    }
    
    fun hideEmptyState(container: View) {
        container.visibility = View.GONE
    }
    
    fun toggleEmptyState(container: View, isEmpty: Boolean, title: String = "", message: String = "") {
        if (isEmpty) {
            showEmptyState(container, title, message)
        } else {
            hideEmptyState(container)
        }
    }
}
