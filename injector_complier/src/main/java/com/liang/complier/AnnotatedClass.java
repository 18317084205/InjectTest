package com.liang.complier;


import com.liang.annotations.ListenerClass;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class AnnotatedClass {

    private final String parameterName;
    private TypeElement typeElement;
    private Elements elements;
    private Map<ListenerClass, List<MethodViewBinding>> listenerClassListMap;
    private boolean isViewListener;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        this.typeElement = typeElement;
        this.elements = elements;
        listenerClassListMap = new HashMap<>();
        parameterName = toLowerCaseFirstOne(typeElement.getSimpleName() + "");
    }

    public void addElement(ListenerClass listenerClass, MethodViewBinding viewBinding) {
        List<MethodViewBinding> elements = listenerClassListMap.get(listenerClass);
        if (elements == null) {
            elements = new ArrayList<>();
            listenerClassListMap.put(listenerClass, elements);
        }
        elements.add(viewBinding);
    }

    public JavaFile generateActivityFile() {
        // build inject method
        MethodSpec.Builder constructorMethod = getMethodSpecBuilder(Containers.CONSTRUCTOR, false);
        constructorMethod.addParameter(TypeName.get(typeElement.asType()), parameterName);
        constructorMethod.addParameter(Containers.VIEW, "view");
        constructorMethod.addStatement("this.$L = $L", parameterName, parameterName);
        constructorMethod.addStatement("this.$L = $L", "view", "view");

        addConstructorSource(constructorMethod);

        MethodSpec.Builder unBindMethod = getMethodSpecBuilder(Containers.METHOD_UNBIND, true);
        addUnBingMethodCode(unBindMethod);

        TypeSpec.Builder injectClass = getTypeSpecBuilder(typeElement.getSimpleName() + Containers.INJECTOR);
        injectClass.addMethod(constructorMethod.build());
        injectClass.addMethod(unBindMethod.build());

        addBingListenerMethodCode(injectClass);

        String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
        return JavaFile.builder(packageName, injectClass.build()).build();
    }

    private void addConstructorSource(MethodSpec.Builder methodSpec) {
        for (ListenerClass listenerClass : listenerClassListMap.keySet()) {
            if (listenerClass.targetType().equals("bindView")) {
                createBingViewCode(methodSpec, listenerClassListMap.get(listenerClass));
                continue;
            }
            isViewListener = true;
        }

        if (!isViewListener) {
            return;
        }

        methodSpec.addStatement("$L(this)", Containers.METHOD_SET_LISTENER);
    }

    private void createBingViewCode(MethodSpec.Builder methodSpec, List<MethodViewBinding> methodViewBindings) {
        for (MethodViewBinding viewBinding : methodViewBindings) {
            CodeBlock.Builder builder = createBingFieldCode(viewBinding);
            methodSpec.addStatement("$L", builder.build());
        }
    }

    private CodeBlock.Builder createBingFieldCode(MethodViewBinding viewBinding) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("$L.$L = ", parameterName, viewBinding.getName());
        builder.add("$T.findViewAsType(view,$L)", Containers.VIEW_UTILS, viewBinding.getIds()[0]);
        return builder;
    }

    private void addUnBingMethodCode(MethodSpec.Builder methodSpec) {
        for (ListenerClass listenerClass : listenerClassListMap.keySet()) {
            if (listenerClass.targetType().equals("bindView")) {
                for (MethodViewBinding viewBinding : listenerClassListMap.get(listenerClass)) {
                    CodeBlock.Builder builder = createUnBingFieldCode(viewBinding);
                    methodSpec.addStatement("$L", builder.build());
                }
                continue;
            }
        }
        createUnBingMethodCode(methodSpec);
    }

    private CodeBlock.Builder createUnBingFieldCode(MethodViewBinding viewBinding) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add("$L.$L = null", parameterName, viewBinding.getName());
        return builder;
    }

    private CodeBlock.Builder createUnBingMethodCode(MethodSpec.Builder methodSpec) {
        CodeBlock.Builder builder = CodeBlock.builder();
        methodSpec.addStatement("$L(null)", Containers.METHOD_SET_LISTENER);
        return builder;
    }

    private void addBingListenerMethodCode(TypeSpec.Builder builder) {
        if (!isViewListener) {
            return;
        }

        MethodSpec.Builder setListenerMethod = getMethodSpecBuilder(Containers.METHOD_SET_LISTENER, false);
        setListenerMethod.addParameter(Containers.VIEW_LISTENER, "listener");

        for (Map.Entry<ListenerClass, List<MethodViewBinding>> entry : listenerClassListMap.entrySet()) {
            ListenerClass listenerClass = entry.getKey();
            if (listenerClass.targetType().equals("bindView")) {
                continue;
            }
            MethodSpec.Builder listenerMethod = createBingListenerMethod(listenerClass);
            listenerMethod.addStatement("$L", createListenerMethodCode(listenerClass, entry.getValue()).build());
            builder.addMethod(listenerMethod.build());

            createBingListenerMethodCode(setListenerMethod, entry.getValue());
        }

        builder.superclass(Containers.VIEW_LISTENER);
        builder.addMethod(setListenerMethod.build());
    }

    private MethodSpec.Builder createBingListenerMethod(ListenerClass listenerClass) {
        MethodSpec.Builder builder = getMethodSpecBuilder(listenerClass.targetType(), true);
        builder.returns(Containers.getTypeName(listenerClass.returnType()));
        String[] parameterTypes = listenerClass.parameters();
        for (int i = 0; i < parameterTypes.length; i++) {
            String parameterType = parameterTypes[i];
            builder.addParameter(Containers.getTypeName(parameterType),
                    parameterType.equals(Containers.VIEW.topLevelClassName().toString()) ? "v" : "p" + i);
        }
        return builder;
    }

    private CodeBlock.Builder createListenerMethodCode(ListenerClass listenerClass, List<MethodViewBinding> methodViewBindings) {
        CodeBlock.Builder builder = CodeBlock.builder().beginControlFlow("switch(v.getId())");
        String[] parameterTypes = listenerClass.parameters();

        for (MethodViewBinding viewBinding : methodViewBindings) {
            for (int value : viewBinding.getIds()) {
                builder.add("case $L:\n", value);
            }
            StringBuilder stringBuilder = new StringBuilder("$L.$L(");
            int index = viewBinding.getViewParameterType() == null ? 1 : 0;
            boolean hasCast = index == 0 && !viewBinding.getViewParameterType().toString().equals(Containers.VIEW.toString());
            for (int i = 0; i < parameterTypes.length; i++) {
                if (index == 1 && i == 0) {
                    continue;
                }
                String parameter = parameterTypes[i];
                stringBuilder.append(parameter.equals(Containers.VIEW.topLevelClassName().toString()) && index == 0 ?
                        (viewBinding.getViewParameterType().toString().equals(Containers.VIEW.topLevelClassName().toString()) ?
                                "v" : "($T)v") : " p" + i);
                if (i < parameterTypes.length - 1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(");");

            if (listenerClass.returnType().equals("void")) {
                if (hasCast) {
                    builder.add(stringBuilder.toString() + "\n", parameterName, viewBinding.getName(), viewBinding.getViewParameterType());
                } else {
                    builder.add(stringBuilder.toString() + "\n", parameterName, viewBinding.getName());
                }
                builder.add("break;\n");
            } else {
                if (hasCast) {
                    builder.add("return " + stringBuilder.toString() + "\n", parameterName, viewBinding.getName(), viewBinding.getViewParameterType());
                } else {
                    builder.add("return " + stringBuilder.toString() + "\n", parameterName, viewBinding.getName());
                }

            }
        }

        if (!listenerClass.returnType().equals("void")) {
            builder.endControlFlow();
            builder.add("return $L", listenerClass.defaultReturn());
        } else {
            builder.add("}");
        }

        return builder;
    }

    private void createBingListenerMethodCode(MethodSpec.Builder builder, List<MethodViewBinding> methodViewBindings) {
        for (MethodViewBinding viewBinding : methodViewBindings) {
            for (int id : viewBinding.getIds()) {
                if (!viewBinding.getSetter().isEmpty()) {
                    builder.addStatement("$T.$L(view,$L,listener)", Containers.VIEW_UTILS, viewBinding.getSetter(), id);
                }
            }
        }
    }

    private TypeSpec.Builder getTypeSpecBuilder(String name) {
        return TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(Containers.UNBIND)
                .addField(TypeName.get(typeElement.asType()), parameterName, Modifier.PRIVATE)
                .addField(Containers.VIEW, "view", Modifier.PRIVATE);
    }

    private MethodSpec.Builder getMethodSpecBuilder(String name, boolean isOverride) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC);
        if (isOverride) {
            builder.addAnnotation(Override.class);
        }
        return builder;
    }

    private String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

}
