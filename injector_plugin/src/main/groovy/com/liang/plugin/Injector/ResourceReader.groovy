package com.liang.plugin.Injector

import kotlin.Unit
import kotlin.io.FilesKt
import kotlin.jvm.functions.Function1
import kotlin.text.Charsets

class ResourceReader {
    RClassBuilder finalRClassBuilder

    ResourceReader(RClassBuilder finalRClassBuilder) {
        this.finalRClassBuilder = finalRClassBuilder
    }


    void readSymbolTable(File symbolTable) {
        FilesKt.forEachLine(symbolTable, Charsets.UTF_8, new Function1<String, Unit>() {
            @Override
            Unit invoke(String s) {
                processLine(s)
                return Unit.INSTANCE
            }
        })
    }

    void processLine(String line) {
        def values = line.split(" ")
        if (values.size() >= 4) {
            def javaType = values[0]
            def symbolType = values[1]
            def fieldName = values[2]
            def fieldValue = values[3]
            if (javaType == "int" && symbolType in RClassBuilder.supported_types) {
                finalRClassBuilder.addResourceField(symbolType, fieldName, fieldValue)
            }
        }
    }

}
