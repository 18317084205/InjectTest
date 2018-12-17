package com.liang.inject;

import android.view.KeyEvent;
import android.view.View;

public abstract class ViewListener {
    public void onClick(View v) {
    }

    public boolean onLongClick(View v) {
        return false;
    }

    public void onCheckedChanged(View v, boolean isChecked) {
    }

    public void onTextChanged(View v, CharSequence s, int start, int before, int count) {
    }

    public boolean onEditorAction(View v, int actionId, KeyEvent event) {
        return false;
    }
}
