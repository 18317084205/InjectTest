package com.liang.plugin.Injector

import com.android.annotations.Nullable
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class RGenerator extends DefaultTask {
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

    @Input
    @Nullable
    String isLibrary;

    @TaskAction
    void brewJava() {
        RClassBuilder finalRClassBuilder = new RClassBuilder(packageName, className)
        if (isLibrary){
            ResourceReader reader = new ResourceReader(finalRClassBuilder)
            reader.readSymbolTable(fileCollection.getSingleFile())
        }
        finalRClassBuilder.build().writeTo(outputDir)
    }
}
