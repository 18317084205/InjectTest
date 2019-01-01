package com.jianbo.libraryb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.liang.annotations.*
import com.liang.inject.JInjector


class TestBActivity : AppCompatActivity() {

    @BindView(R2.id.kot_button)
    lateinit var button: Button

    @BindView(R2.id.kot_imageView)
    lateinit var imageView: ImageView

    @BindView(R2.id.kot_textView)
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testb)
        JInjector.bind(this)
        
    }

    @OnClick(R2.id.kot_button, R2.id.kot_imageView)
    fun kot(view: View) {
        Log.e("kotActivity", "view: " + view.id)
        var msg = ""
        if (view.id == R.id.kot_button) {
            msg = "button"
        }

        if (view.id == R.id.kot_imageView) {
            msg = "imageView"
        }
        showMsg("OnClick: $msg")
    }

    @OnClick(R2.id.kot_button, R2.id.kot_imageView)
    fun kot3(view: View) {
        var msg = ""
        if (view.id == R.id.kot_button) {
            msg = "button"
        }

        if (view.id == R.id.kot_imageView) {
            msg = "imageView"
        }
        showMsg("OnClick: $msg")
    }

    @OnLongClick(R2.id.kot_button, R2.id.kot_imageView)
    fun kot2(view: View): Boolean {
        var msg = ""
        if (view.id == R.id.kot_button) {
            msg = "button"
        }

        if (view.id == R.id.kot_imageView) {
            msg = "imageView"
        }
        showMsg("OnLongClick: $msg")
        return true
    }

    @OnCheckedChanged(R2.id.kot_checkBox, R2.id.kot_switch1, R2.id.kot_toggleButton, R2.id.kot_radioButton1, R2.id.kot_radioButton2)
    fun kotCheckedChange(v: CompoundButton, isChecked: Boolean) {
        Log.d("OnCheckedChanged", "kotCheckedChange: " + v.text + ", isChecked: " + isChecked)
        var msg = ""
        if (v.id == R.id.kot_checkBox) {
            msg = "checkBox"
        }

        if (v.id == R.id.kot_switch1) {
            msg = "switch1"
        }

        if (v.id == R.id.kot_toggleButton) {
            msg = "toggleButton"
        }

        if (v.id == R.id.kot_radioButton1) {
            msg = "radioButton1"
        }
        if (v.id == R.id.kot_radioButton2) {
            msg = "radioButton2"
        }

        showMsg("OnCheckedChanged: $msg\nisChecked: $isChecked")
    }

    @OnTextChanged(R2.id.kot_editText)
    fun kotTextChanged(v: View, s: CharSequence, start: Int, before: Int, count: Int) {
        showMsg("kotTextChanged: $s")
    }

    @OnEditorAction(R2.id.kot_editText)
    fun kotEditorAction(v: View, actionId: Int, event: KeyEvent): Boolean {
        showMsg("kotEditorAction: " + (v as EditText).text)
        return true
    }

    private fun showMsg(msg: String) {
        textView?.text = msg
    }
}
