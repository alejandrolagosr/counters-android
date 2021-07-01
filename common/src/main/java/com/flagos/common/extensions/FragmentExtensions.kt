package com.flagos.common.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.flagos.common.R

fun Fragment.setUpFragmentToolBar(toolBar: Toolbar, title: String, toolbarType: ToolbarType) {
    toolBar.findViewById<TextView>(R.id.toolbar_title).apply { text = title }
    (requireActivity() as AppCompatActivity).apply {
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (toolbarType == ToolbarType.WITH_CLOSE_BUTTON) {
            supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.ic_close))
        }
    }
}

fun Fragment.showKeyboard(): Boolean {
    return (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput((activity?.currentFocus ?: View(context)), 0)
}

fun Fragment.hideKeyboard(): Boolean {
    return (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow((activity?.currentFocus ?: view?.rootView ?: View(context)).windowToken, 0)
}

enum class ToolbarType { WITH_CLOSE_BUTTON, WITH_BACK_BUTTON }