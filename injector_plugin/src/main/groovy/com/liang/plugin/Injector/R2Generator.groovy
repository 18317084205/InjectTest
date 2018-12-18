package com.liang.plugin.Injector

import com.android.annotations.Nullable
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class R2Generator extends DefaultTask{
    @Nullable
    private File outputDir;
    @Nullable
    private FileCollection fileCollection;
    @Nullable
    private Boolean useAndroidX;
    @Nullable
    private String packageName;
    @Nullable
    private String className;


    @OutputDirectory
    @Nullable
    File getOutputDir() {
        return this.outputDir;
    }

    void setOutputDir(@Nullable File var1) {
        this.outputDir = var1;
    }

    @InputFiles
    @Nullable
    FileCollection getFileCollection() {
        return this.fileCollection;
    }

    void setFileCollection(@Nullable FileCollection var1) {
        this.fileCollection = var1;
    }

    @Input
    @Nullable
    Boolean getUseAndroidX() {
        return this.useAndroidX;
    }

    void setUseAndroidX(@Nullable Boolean var1) {
        this.useAndroidX = var1;
    }

    @Input
    @Nullable
    String getPackageName() {
        return this.packageName;
    }

    void setPackageName(@Nullable String var1) {
        this.packageName = var1;
    }

    @Input
    @Nullable
    String getClassName() {
        return this.className;
    }

    void setClassName(@Nullable String var1) {
        this.className = var1;
    }

    @TaskAction
     void brewJava() {
        FinalRClassBuilder finalRClassBuilder = new FinalRClassBuilder(packageName, className, useAndroidX);
        ResourceSymbolListReader(finalRClassBuilder).readSymbolTable(fileCollection.getSingleFile())
        finalRClassBuilder.build().writeTo(outputDir)
    }
}
