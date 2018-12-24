package com.liang.plugin.Injector

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.sun.xml.internal.ws.util.StringUtils

import javax.lang.model.element.Modifier
import java.lang.reflect.Type

class RClassBuilder {

    final DEF_ANNOTATION_PACKAGE = "android.support.annotation"
    static final supported_types = ["anim", "array", "attr", "bool", "color", "dimen", "drawable", "id",
                             "integer", "layout", "menu", "plurals", "string", "style", "styleable"]
    def packageName
    def className
    def resourceTypes = new HashMap<String, TypeSpec.Builder>();

    RClassBuilder(String packageName, String className) {
        this.packageName = packageName
        this.className = className
    }

    JavaFile build() {
        def result = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        Iterator iterator = supported_types.iterator()
        while (iterator.hasNext()) {
            def type = iterator.next().toString()
            def builder = resourceTypes.get(type);
            if (builder != null) {
                result.addType(builder.build());
            }
        }

        return JavaFile.builder(packageName, result.build())
                .addFileComment("AUTO-GENERATED FILE.  DO NOT MODIFY.")
                .build()
    }

    void addResourceField(String type, String fieldName, String fieldValue) {
        if (!type in supported_types) {
            return
        }

        Modifier[] modifiers = [Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL]
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(TypeName.INT, fieldName)
                .addModifiers(modifiers)
                .initializer(fieldValue)

//        fieldSpecBuilder.addAnnotation(getSupportAnnotationClass(type))
        TypeSpec.Builder builder = resourceTypes.get(type)
        if (builder == null) {
            builder = TypeSpec.classBuilder(type).addModifiers(modifiers)
//            builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE))
            resourceTypes.put(type, builder)
        }
        builder.addField(fieldSpecBuilder.build())
    }

    ClassName getSupportAnnotationClass(String type) {
        return ClassName.get(DEF_ANNOTATION_PACKAGE, StringUtils.capitalize(type) + "Res", new String[0])
    }

}
