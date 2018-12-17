package com.liang.inject;

import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ViewUtils {
    private static boolean enabled = true;
    private static final Runnable ENABLE_AGAIN = new Runnable() {
        @Override
        public void run() {
            enabled = true;
        }
    };

    public static <T extends View> T findViewAsType(View view, @IdRes int id) {
        return (T) view.findViewById(id);//强转
    }

    public static void setOnClick(View view, @IdRes int id, final ViewListener listener) {
        view.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enabled) {
                    enabled = false;
                    v.post(ENABLE_AGAIN);
                    if (listener != null) {
                        listener.onClick(v);
                    }
                }
            }
        } );
    }

    public static void setOnLongClick(View view, @IdRes int id, final ViewListener listener) {
        view.findViewById(id).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    return listener.onLongClick(v);
                }
                return false;
            }
        });
    }

    public static void setOnCheckedChange(View view, @IdRes int id, final ViewListener listener) {
        View v = view.findViewById(id);
        if (v instanceof CompoundButton) {
            ((CompoundButton) v).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null) {
                        listener.onCheckedChanged(buttonView, isChecked);
                    }
                }
            });
        }
    }

    public static void addTextChanged(View view, @IdRes int id, final ViewListener listener) {
        final View v = view.findViewById(id);
        if (v instanceof TextView) {
            ((TextView) v).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (listener != null) {
                        listener.onTextChanged(v, s, start, before, count);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    public static void setOnEditorAction(View view, @IdRes int id, final ViewListener listener) {
        View v = view.findViewById(id);
        if (v instanceof TextView) {
            ((TextView) v).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (listener != null) {
                        return listener.onEditorAction(v, actionId, event);
                    }
                    return false;
                }
            });
        }
    }

}
