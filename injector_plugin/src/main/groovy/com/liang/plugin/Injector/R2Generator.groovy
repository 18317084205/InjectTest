package com.liang.plugin.Injector

import com.android.annotations.Nullable
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class R2Generator extends DefaultTask{
    @OutputDirectory
    @Nullable
    File outputDir;

    @InputFiles
    @Nullable
    FileCollection fileCollection

    @Input
    @Nullable
    String packageName;

    @Input
    @Nullable
    String className;


    @TaskAction
     void brewJava() {
        FinalRClassBuilder finalRClassBuilder = new FinalRClassBuilder(packageName, className)
        ResourceSymbolListReader reader = new ResourceSymbolListReader(finalRClassBuilder)
        reader.readSymbolTable(fileCollection.getSingleFile())
        finalRClassBuilder.build().writeTo(outputDir)
    }
}
