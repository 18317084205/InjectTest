package com.liang.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
@ListenerClass(targetType = "bindView", setter = "")
public @interface BindView {
    /** View ID to which the field will be bound. */
     int[] value() default {};
}