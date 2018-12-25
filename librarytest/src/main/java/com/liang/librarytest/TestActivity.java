package com.liang.librarytest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.liang.annotations.BindView;
import com.liang.annotations.OnCheckedChanged;
import com.liang.annotations.OnClick;
import com.liang.annotations.OnEditorAction;
import com.liang.annotations.OnLongClick;
import com.liang.annotations.OnTextChanged;
import com.liang.inject.JInjector;

public class TestActivity extends AppCompatActivity {

    @BindView(R2.id.button)
    Button button;

    @BindView(R2.id.imageView)
    ImageView imageView;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        JInjector.bind(this);
        int res = R.id.button;
        imageView.setColorFilter(Color.GREEN);
    }

//    @OnClick({R.id.button, R.id.imageView})
//    public void test(View view) {
//        String msg = "";
//        if (view.getId() == R.id.button){
//            msg = "button";
//        }
//
//        if (view.getId() == R.id.imageView){
//            msg = "imageView";
//        }
//        showMsg("OnClick: " + msg);
//    }
//
//    @OnClick({R.id.button, R.id.imageView})
//    public void test3(View view) {
//        String msg = "";
//        if (view.getId() == R.id.button){
//            msg = "button";
//        }
//
//        if (view.getId() == R.id.imageView){
//            msg = "imageView";
//        }
//        showMsg("OnClick: " + msg);
//    }
//
//    @OnLongClick({R.id.button, R.id.imageView})
//    public boolean test2(View view) {
//        String msg = "";
//        if (view.getId() == R.id.button){
//            msg = "button";
//        }
//
//        if (view.getId() == R.id.imageView){
//            msg = "imageView";
//        }
//        showMsg("OnLongClick: " + msg);
//        return true;
//    }
//
//    @OnCheckedChanged({R.id.checkBox, R.id.switch1, R.id.toggleButton, R.id.radioButton1, R.id.radioButton2})
//    public void testCheckedChange(CompoundButton v, boolean isChecked) {
//        Log.d("OnCheckedChanged", "testCheckedChange: " + v.getText() + ", isChecked: " + isChecked);
//        String msg = "";
//        if (v.getId() == R.id.checkBox){
//            msg = "checkBox";
//        }
//
//        if (v.getId() == R.id.switch1){
//            msg = "switch1";
//        }
//
//        if (v.getId() == R.id.toggleButton){
//            msg = "toggleButton";
//        }
//
//        if (v.getId() == R.id.radioButton1){
//            msg = "radioButton1";
//        }
//        if (v.getId() == R.id.radioButton2){
//            msg = "radioButton2";
//        }
//
//        showMsg("OnCheckedChanged: " + msg + "\nisChecked: " + isChecked);
//    }
//
//    @OnTextChanged(R.id.editText)
//    public void testTextChanged(View v, CharSequence s, int start, int before, int count) {
//        showMsg("testTextChanged: " + s);
//    }
//
//    @OnEditorAction(R.id.editText)
//    public boolean testEditorAction(View v, int actionId, KeyEvent event) {
//        showMsg("testEditorAction: " + ((EditText) v).getText());
//        return true;
//    }

    private void showMsg(String msg) {
        if (toast != null) {
            toast.setText(msg);
        } else {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
