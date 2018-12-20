package com.liang.complier;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

public class MethodViewBinding {
    private final String name;
    private final List<String> resIds = new ArrayList<>();
    private final String setter;
    private TypeName viewTypeName;
    public MethodViewBinding(String name, List<String> resIds, String setter) {
        this.name = name;
        this.resIds.clear();
        this.resIds.addAll(resIds);
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public List<String> getIds() {
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
