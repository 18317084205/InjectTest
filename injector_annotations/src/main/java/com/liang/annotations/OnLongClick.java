package com.liang.annotations;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@ListenerClass(
        targetType = "onLongClick",
        setter = "setOnLongClick",
        parameters = "android.view.View",
        returnType = "boolean",
        defaultReturn = "false"
)
public @interface OnLongClick {
    @IdRes int[] value() default { -1 };
}
