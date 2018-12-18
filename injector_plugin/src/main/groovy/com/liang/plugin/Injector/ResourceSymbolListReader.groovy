package com.liang.plugin.Injector

import groovyjarjarantlr.StringUtils
import kotlin.Unit
import kotlin.io.FilesKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.text.Charsets
import kotlin.text.StringsKt;

class ResourceSymbolListReader {
    def finalRClassBuilder

    ResourceSymbolListReader(finalRClassBuilder) {
        this.finalRClassBuilder = finalRClassBuilder
    }


    void readSymbolTable(File symbolTable) {
        symbolTable.for
        BufferedReader(InputStreamReader(FileInputStream(this), charset)).forEachLine(action)

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
            if (javaType == "int" && symbolType in FinalRClassBuilder.supported_types) {
                finalRClassBuilder.addResourceField(symbolType, values[2], values[3])
            }
        }
    }

}
