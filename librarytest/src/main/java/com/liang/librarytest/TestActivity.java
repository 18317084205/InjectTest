package com.liang.librarytest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.annotations.BindView;
import com.liang.annotations.OnCheckedChanged;
import com.liang.annotations.OnClick;
import com.liang.annotations.OnEditorAction;
import com.liang.annotations.OnLongClick;
import com.liang.annotations.OnTextChanged;
import com.liang.inject.JInjector;

public class TestActivity extends AppCompatActivity {

    @BindView(R2.id.test_button)
    Button button;

    @BindView(R2.id.test_imageView)
    ImageView imageView;

    @BindView(R2.id.test_textView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        JInjector.bind(this);
        imageView.setColorFilter(Color.GREEN);
    }

    @OnClick({R2.id.test_button, R2.id.test_imageView})
    public void test(View view) {
        Log.e("TestActivity", "view: " + view.getId());
        String msg = "";
        if (view.getId() == R.id.test_button) {
            msg = "button";
        }

        if (view.getId() == R.id.test_imageView) {
            msg = "imageView";
        }
        showMsg("OnClick: " + msg);
    }

    @OnClick({R2.id.test_button, R2.id.test_imageView})
    public void test3(View view) {
        String msg = "";
        if (view.getId() == R.id.test_button) {
            msg = "button";
        }

        if (view.getId() == R.id.test_imageView) {
            msg = "imageView";
        }
        showMsg("OnClick: " + msg);
    }

    @OnLongClick({R2.id.test_button, R2.id.test_imageView})
    public boolean test2(View view) {
        String msg = "";
        if (view.getId() == R.id.test_button) {
            msg = "button";
        }

        if (view.getId() == R.id.test_imageView) {
            msg = "imageView";
        }
        showMsg("OnLongClick: " + msg);
        return true;
    }

    @OnCheckedChanged({R2.id.test_checkBox, R2.id.test_switch1, R2.id.test_toggleButton, R2.id.test_radioButton1, R2.id.test_radioButton2})
    public void testCheckedChange(CompoundButton v, boolean isChecked) {
        Log.d("OnCheckedChanged", "testCheckedChange: " + v.getText() + ", isChecked: " + isChecked);
        String msg = "";
        if (v.getId() == R.id.test_checkBox) {
            msg = "checkBox";
        }

        if (v.getId() == R.id.test_switch1) {
            msg = "switch1";
        }

        if (v.getId() == R.id.test_toggleButton) {
            msg = "toggleButton";
        }

        if (v.getId() == R.id.test_radioButton1) {
            msg = "radioButton1";
        }
        if (v.getId() == R.id.test_radioButton2) {
            msg = "radioButton2";
        }

        showMsg("OnCheckedChanged: " + msg + "\nisChecked: " + isChecked);
    }

    @OnTextChanged(R2.id.test_editText)
    public void testTextChanged(View v, CharSequence s, int start, int before, int count) {
        showMsg("testTextChanged: " + s);
    }

    @OnEditorAction(R2.id.test_editText)
    public boolean testEditorAction(View v, int actionId, KeyEvent event) {
        showMsg("testEditorAction: " + ((EditText) v).getText());
        return true;
    }

    private void showMsg(String msg) {
        textView.setText(msg);
    }
}
