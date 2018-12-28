package com.jianbo.libraryb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.liang.annotations.BindView
import com.liang.inject.JInjector


class TestBActivity : AppCompatActivity() {

    @BindView(R2.id.button)
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        JInjector.bind(this)
        button.setOnClickListener {
            Toast.makeText(this, "BindView", Toast.LENGTH_SHORT).show()
        }
    }
}
