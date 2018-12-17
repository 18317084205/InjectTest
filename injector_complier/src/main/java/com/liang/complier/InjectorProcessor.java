package com.liang.complier;

import com.google.auto.service.AutoService;
import com.liang.annotations.BindView;
import com.liang.annotations.ListenerClass;
import com.liang.annotations.OnCheckedChanged;
import com.liang.annotations.OnClick;
import com.liang.annotations.OnEditorAction;
import com.liang.annotations.OnLongClick;
import com.liang.annotations.OnTextChanged;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class InjectorProcessor extends AbstractProcessor {
    private Filer filer; //文件相关的辅助类
    private Elements elements; //元素相关的辅助类
    private Messager messager; //日志相关的辅助类
    private Map<String, AnnotatedClass> annotatedClassMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        annotatedClassMap = new TreeMap<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : Containers.getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        log("InjectorProcessor: %s", "process...");
        annotatedClassMap.clear();

        for (Class<? extends Annotation> annotation : Containers.getSupportedAnnotations()) {
            process(roundEnvironment, annotation);
        }

        for (AnnotatedClass annotatedClass : annotatedClassMap.values()) {
            try {
                annotatedClass.generateActivityFile().writeTo(filer);
            } catch (IOException e) {
                error("Generate file failed, reason: %s", e.getMessage());
            }
        }
        return true;
    }

    private void process(RoundEnvironment roundEnvironment, Class<? extends Annotation> clazz) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(clazz)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            ListenerClass listener = clazz.getAnnotation(ListenerClass.class);
            Annotation annotation = element.getAnnotation(clazz);
            int[] ids = {-1};
            if (annotation instanceof BindView) {
                ids = ((BindView) annotation).value();
            }
            if (annotation instanceof OnClick) {
                ids = ((OnClick) annotation).value();
            }
            if (annotation instanceof OnLongClick) {
                ids = ((OnLongClick) annotation).value();
            }
            if (annotation instanceof OnCheckedChanged) {
                ids = ((OnCheckedChanged) annotation).value();
            }
            if (annotation instanceof OnEditorAction) {
                ids = ((OnEditorAction) annotation).value();
            }
            if (annotation instanceof OnTextChanged) {
                ids = ((OnTextChanged) annotation).value();
            }

            if (listener != null) {
                MethodViewBinding viewBinding = new MethodViewBinding(element.getSimpleName().toString(), ids, listener.setter());
                if (element.getKind() == ElementKind.METHOD && element instanceof ExecutableElement) {
                    log("parameterTypeName  getKind: %s", element.getKind().name());
                    List<? extends VariableElement> methodParameters = ((ExecutableElement) element).getParameters();
                    if (!methodParameters.isEmpty() && methodParameters.size() <= listener.parameters().length) {
                        log("parameterTypeName  methodParameters: %s", methodParameters.size());
                        VariableElement variableElement = methodParameters.get(0);
                        TypeMirror parameterType = variableElement.asType();
                        log("parameterTypeName parameterType: %s", parameterType.toString());
                        TypeName parameterTypeName = TypeName.get(parameterType);
                        log("parameterTypeName parameterTypeName: %s", parameterTypeName.toString());
                        viewBinding.setViewTypeName(parameterTypeName);
                    }
                }
                annotatedClass.addElement(listener, viewBinding);
            }
        }
    }

    private AnnotatedClass getAnnotatedClass(Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String fullName = typeElement.getQualifiedName().toString();
        AnnotatedClass annotatedClass = annotatedClassMap.get(fullName);
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(typeElement, elements);
            annotatedClassMap.put(fullName, annotatedClass);
        }
        return annotatedClass;
    }


    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void log(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
