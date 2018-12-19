package com.liang.plugin.Injector

import kotlin.Unit
import kotlin.io.FilesKt
import kotlin.jvm.functions.Function1
import kotlin.text.Charsets

class ResourceSymbolListReader {
    FinalRClassBuilder finalRClassBuilder

    ResourceSymbolListReader(FinalRClassBuilder finalRClassBuilder) {
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
            def fieldValue = "R.id.$fieldName"
            if (javaType == "int" && symbolType in FinalRClassBuilder.supported_types) {
                print("processLine ... symbolType:$symbolType" + "... fieldName:$fieldName"
                        + "... fieldValue:$fieldValue\n")
                finalRClassBuilder.addResourceField(symbolType, fieldName, fieldValue)
            }
        }
    }

}
