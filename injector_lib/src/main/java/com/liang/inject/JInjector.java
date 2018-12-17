package com.liang.inject;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JInjector {

    static final String TAG = JInjector.class.getSimpleName();
    static final String INJECTOR = "$$Injector";
    @VisibleForTesting
    static final Map<Class<?>, Constructor<? extends UnBinder>> BINDINGS = new LinkedHashMap<>();

    public static UnBinder bind(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView(); //获取devoreView
        return createBinding(target, sourceView); //执行绑定操作
    }

    private static UnBinder createBinding(Activity target, View sourceView) {
        Class<?> targetClass = target.getClass();
        Log.d(TAG, "Looking up binding for " + targetClass.getName());
        Constructor<? extends UnBinder> constructor = findBindingConstructorForClass(targetClass);//查找合适的构造器
        if (constructor == null) {
            return UnBinder.EMPTY;
        }
        try {
            //通过反射创建的实例 MainActivity_ViewBinding(final MainActivity target, View source)
            return constructor.newInstance(target,sourceView);
        }catch (IllegalAccessException e) {
            throw new RuntimeException("UnBinder to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("UnBinder to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("UnBinder to create binding instance.", cause);
        }
    }

    private static Constructor<? extends UnBinder> findBindingConstructorForClass(Class<?> targetClass) {

        Constructor<? extends UnBinder> bindingCtor = BINDINGS.get(targetClass);
        if (bindingCtor != null || BINDINGS.containsKey(targetClass)) {
            Log.d(TAG, "HIT: Cached in binding map.");
            return bindingCtor;
        }
        String clsName = targetClass.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            Log.e(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
        try {
            Class<?> bindingClass = targetClass.getClassLoader().loadClass(clsName + INJECTOR);
            bindingCtor = (Constructor<? extends UnBinder>) bindingClass.getConstructor(targetClass, View.class);
            Log.d(TAG, "HIT: Loaded binding class and constructor.");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Not found. Trying superclass " + targetClass.getSuperclass().getName());
            bindingCtor = findBindingConstructorForClass(targetClass.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(targetClass, bindingCtor);
        return bindingCtor;
    }
}
