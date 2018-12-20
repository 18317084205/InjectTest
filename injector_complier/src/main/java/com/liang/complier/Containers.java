package com.liang.complier;

import com.liang.annotations.BindView;
import com.liang.annotations.OnCheckedChanged;
import com.liang.annotations.OnClick;
import com.liang.annotations.OnEditorAction;
import com.liang.annotations.OnLongClick;
import com.liang.annotations.OnTextChanged;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class Containers {

    public static final ClassName UNBIND = ClassName.get("com.liang.inject", "UnBinder");
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName VIEW_UTILS = ClassName.get("com.liang.inject", "ViewUtils");
    public static final ClassName VIEW_LISTENER = ClassName.get("com.liang.inject", "ViewListener");

    public static final String CONSTRUCTOR = "<init>";
    public static final String INJECTOR = "$$Injector";
    public static final String METHOD_UNBIND = "unbind";
    public static final String METHOD_SET_LISTENER = "setViewListener";
    private static final ResTreeScanner RES_TREE_SCANNER = new ResTreeScanner();

    public static Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class);
        annotations.add(OnClick.class);
        annotations.add(OnLongClick.class);
        annotations.add(OnCheckedChanged.class);
        annotations.add(OnEditorAction.class);
        annotations.add(OnTextChanged.class);
        return annotations;
    }

    public static TypeName getTypeName(String returnType) {
        switch (returnType) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                return ClassName.bestGuess(returnType);
        }
    }

    public static List<String> elementToIdRes(Trees trees, Element element,
                                              Class<? extends Annotation> annotation, int[] values) {
        RES_TREE_SCANNER.reset();
        JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
        if (tree != null) {
            tree.accept(RES_TREE_SCANNER);
        } else {
            RES_TREE_SCANNER.defResIds(values);
        }
        return RES_TREE_SCANNER.resourceIds;
    }

    private static AnnotationMirror getMirror(Element element,
                                              Class<? extends Annotation> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }
}
