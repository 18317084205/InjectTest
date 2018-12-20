package com.liang.plugin.Injector

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.sun.xml.internal.ws.util.StringUtils

import javax.lang.model.element.Modifier
import java.lang.reflect.Type

class FinalRClassBuilder {

    final annotation_package = "android.support.annotation"
    static final supported_types = ["id"]
    def packageName
    def className
    def resourceTypes = new HashMap<String, TypeSpec.Builder>();

    FinalRClassBuilder(String packageName,String className) {
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
                .addFileComment("Generated code from Butter Knife gradle plugin. Do not modify!")
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
            resourceTypes.put(type, builder)
        }

        builder.addField(fieldSpecBuilder.build())

    }

    ClassName getSupportAnnotationClass(String type) {
        return ClassName.get(annotation_package, StringUtils.capitalize(type) + "Res",new String[0])
    }

}
