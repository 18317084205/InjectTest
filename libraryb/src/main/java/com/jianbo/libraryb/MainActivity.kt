package com.jianbo.libraryb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.liang.annotations.BindView

class MainActivity : AppCompatActivity() {

    @BindView(R2.id.button)
    var button:Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
