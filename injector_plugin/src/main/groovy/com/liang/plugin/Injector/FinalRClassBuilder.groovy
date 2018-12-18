package com.liang.plugin.Injector

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec

import javax.lang.model.element.Modifier

class FinalRClassBuilder {

    final annotation_package = "androidx.annotation"
    final annotation_package_legacy = "android.support.annotation"
    final supported_types = ["anim", "array", "attr", "bool", "color", "dimen",
                                         "drawable", "id", "integer", "layout", "menu", "plurals", "string", "style", "styleable"]
    private def packageName
    private def className
    private def useLegacyTypes
    private def resourceTypes = new HashMap<String, TypeSpec.Builder>();

    FinalRClassBuilder(packageName, className, useLegacyTypes) {
        this.packageName = packageName
        this.className = className
        this.useLegacyTypes = useLegacyTypes
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

    void addResourceField(type, fieldName, fieldValue) {
        if (!type in supported_types) {
            return
        }

        fieldSpecBuilder = FieldSpec.builder(TypeName.INT, fieldName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(fieldValue)

        fieldSpecBuilder.addAnnotation(getSupportAnnotationClass(type))
    }

    ClassName getSupportAnnotationClass(type) {
        supportPackage = useLegacyTypes ? annotation_package_legacy : annotation_package
        return ClassName.get(supportPackage, type.capitalize() + "Res")
    }

}
