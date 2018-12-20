package com.liang.annotations;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "onEditorAction",
        setter = "setOnEditorAction",
        parameters = {
                "android.view.View",
                "int",
                "android.view.KeyEvent"
        },
        returnType = "boolean",
        defaultReturn = "false"
)
public @interface OnEditorAction {
    @IdRes int[] value() default {};
}
