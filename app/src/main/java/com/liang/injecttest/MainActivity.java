package com.liang.injecttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jianbo.libraryb.TestBActivity;
import com.liang.annotations.OnClick;
import com.liang.inject.JInjector;
import com.liang.librarytest.TestActivity;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JInjector.bind(this);
    }

    @OnClick({R.id.button, R.id.button2})
    public void test(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(MainActivity.this, TestBActivity.class));
                break;
        }
    }


}
