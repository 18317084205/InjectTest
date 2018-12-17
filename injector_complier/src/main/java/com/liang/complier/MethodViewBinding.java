package com.liang.complier;

import com.squareup.javapoet.TypeName;

public class MethodViewBinding {
    private final String name;
    private final int[] resIds;
    private final String setter;
    private TypeName viewTypeName;
    public MethodViewBinding(String name, int[] resIds, String setter) {
        this.name = name;
        this.resIds = resIds;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public int[] getIds() {
        return resIds;
    }

    public String getSetter() {
        return setter;
    }

    public void setViewTypeName(TypeName viewTypeName) {
        this.viewTypeName = viewTypeName;
    }

    public TypeName getViewParameterType() {
        return viewTypeName;
    }
}
