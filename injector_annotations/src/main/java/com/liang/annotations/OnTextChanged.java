package com.liang.annotations;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "onTextChanged",
        setter = "addTextChanged",
        parameters = {
                "android.view.View",
                "java.lang.CharSequence",
                "int",
                "int",
                "int"
        }
)
public @interface OnTextChanged {
    @IdRes int[] value() default {-1};
}
