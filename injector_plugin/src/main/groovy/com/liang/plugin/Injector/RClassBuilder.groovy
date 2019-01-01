package com.liang.plugin.Injector

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.sun.xml.internal.ws.util.StringUtils
import javax.lang.model.element.Modifier

class RClassBuilder {

    final DEF_ANNOTATION_PACKAGE = 'android.support.annotation'
//    static final supported_types = ["anim", "array", "attr", "bool", "color", "dimen", "drawable", "id",
//                             "integer", "layout", "menu", "plurals", "string", "style", "styleable"]
    static final R2_CLASS_NAME = 'R2'
    static final R2_CLASS_TYPE = new TypeName(R2_CLASS_NAME)
    static final supported_types = ['id']
    static final INIT_TYPE = 'getInstance'
    static final MAP_TYPE = 'ids'
    static final INIT_ID_TYPE = 'initIds'
    static final HOLDER_TYPE = 'R2Holder'
    static final INSTANCE_TYPE = 'INSTANCE'


    def packageName
    def className
    def resourceTypes = new HashMap<String, TypeSpec.Builder>();
    def resourceMethodSpecTypes = new HashMap<String, MethodSpec.Builder>();

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

        // 泛型的类型
        ClassName kay = ClassName.get('java.lang', 'Integer')
        ClassName value = ClassName.get('java.lang', 'Integer')
        // map类型
        ClassName map = ClassName.get('java.util', 'Map')
        // HashMap类型
        ClassName hashMap = ClassName.get('java.util', 'HashMap')
        // 生成Map类型，类型的名称、Key、Value
        TypeName mapType = ParameterizedTypeName.get(map, kay, value)
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(mapType, MAP_TYPE)
                .addModifiers(Modifier.PRIVATE)
                .initializer('new $T<>()', hashMap)
        result.addField(fieldSpecBuilder.build())

        result.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).addStatement('$L()', INIT_ID_TYPE).build())

        result.addMethod(MethodSpec.methodBuilder("getId").addModifiers(Modifier.PUBLIC)
                .returns(value).addParameter(TypeName.INT, 'r2Id')
                .addStatement('return $L.containsKey(r2Id) ? $L.get(r2Id) : 0', MAP_TYPE, MAP_TYPE)
                .build())

        result.addMethod(MethodSpec.methodBuilder(INIT_TYPE).addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement('return $L.$L', HOLDER_TYPE, INSTANCE_TYPE)
                .returns(R2_CLASS_TYPE).build())

        result.addType(TypeSpec.classBuilder(HOLDER_TYPE).addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addField(FieldSpec.builder(R2_CLASS_TYPE, INSTANCE_TYPE).initializer('new $T()', R2_CLASS_TYPE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL).build())
                .build())

        def initIds = resourceMethodSpecTypes.get(INIT_ID_TYPE);
        if (initIds == null) {
            initIds = MethodSpec.methodBuilder(INIT_ID_TYPE).addModifiers(Modifier.PRIVATE)
        }
        result.addMethod(initIds.build());
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

        MethodSpec.Builder initBuilder = resourceMethodSpecTypes.get(INIT_ID_TYPE)
        if (initBuilder == null) {
            initBuilder = MethodSpec.methodBuilder(INIT_ID_TYPE).addModifiers(Modifier.PRIVATE)
            resourceMethodSpecTypes.put(INIT_ID_TYPE, initBuilder)
        }
        initBuilder.addStatement('$L.put($L, R.$L.$L)', MAP_TYPE, fieldValue, type, fieldName)

    }

    ClassName getSupportAnnotationClass(String type) {
        return ClassName.get(DEF_ANNOTATION_PACKAGE, StringUtils.capitalize(type) + "Res", new String[0])
    }

}
