package com.liang.plugin.Injector

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.android.builder.model.SourceProvider
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler

import java.util.concurrent.atomic.AtomicBoolean

class Injector implements Plugin<Project> {
    final def KOTLIN_PLUGIN = 'kotlin-gradle-plugin'
//    def versions = "1.0.4"
    def isLib

    @Override
    void apply(Project project) {

        project.logger.debug("apply project ...")

        if (!project.android) {
            throw new IllegalStateException("'android-application' or 'android-library' plugin required.")
        }

        def isApp = project.plugins.withType(AppPlugin)
        isLib = project.plugins.withType(LibraryPlugin)
        if (!isApp && !isLib) {
            throw new IllegalStateException("'android-application' or 'android-library' plugin required.")
        }

        def variants = isLib ? project.android.libraryVariants : project.android.applicationVariants
        configureR2Generation(project, variants)
//        ProjectTest.test(project)

        def isKotlin = checkKotlin(project)

        println("project isKotlin->" + isKotlin)

//        if (isKotlin) {
//            project.apply plugin: 'kotlin-kapt'
//        }
//
//        project.dependencies {
//            implementation "org.liang:injector:${versions}"
//            implementation "org.liang:injector_annotations:${versions}"
//            if (isKotlin) {
//                kapt "org.liang:injector_complier:$versions"
//            } else {
//                annotationProcessor "org.liang:injector_complier:${versions}"
//            }
//        }
    }

    boolean checkKotlin(Project project) {
        def dependencies = project.parent.buildscript.configurations.
                getByName(ScriptHandler.CLASSPATH_CONFIGURATION).dependencies
        dependencies.each {
            if (it.name == KOTLIN_PLUGIN) {
                return true
            }
        }
    }

    void configureR2Generation(Project project, DomainObjectSet<BaseVariant> variants) {
        variants.all { variant ->
            String packageName = getPackageName(variant)
            AtomicBoolean atomicBoolean = new AtomicBoolean()
            variant.outputs.all { output ->
                ProcessAndroidResources processResources = output.processResources
                if (atomicBoolean.compareAndSet(false, true)) {
                    def file
                    if (processResources instanceof GenerateLibraryRFileTask) {
                        file = ((GenerateLibraryRFileTask) processResources).textSymbolOutputFile
                    } else if (processResources instanceof LinkApplicationAndroidResourcesTask) {
                        file = ((LinkApplicationAndroidResourcesTask) processResources).textSymbolOutputFile
                    } else {
                        throw (Throwable) (new RuntimeException("Minimum supported Android Gradle Plugin is 3.1.0"))
                    }

                    def injectorRFile = project.files(file).builtBy(processResources)
                    def outputDir = processResources.sourceOutputDir
                    project.tasks.create("generate${variant.name.capitalize()}NewR", RGenerator.class, new Action<RGenerator>() {
                        @Override
                        void execute(RGenerator generator) {
                            generator.outputDir = outputDir
                            generator.fileCollection = injectorRFile
                            generator.packageName = packageName
                            generator.className = "R2"
                            generator.isLibrary = isLib
                            variant.registerJavaGeneratingTask(generator, outputDir)
                        }
                    })

                }
            }
        }
    }

    String getPackageName(BaseVariant variant) {
        XmlSlurper scarper = new XmlSlurper(false, false)
        List list = variant.getSourceSets()
        Iterable iterable = (Iterable) list
        Collection collection = new ArrayList()
        Iterator iterator = iterable.iterator()

        while (iterator.hasNext()) {
            Object obj = iterator.next()
            SourceProvider sourceProvider = (SourceProvider) obj
            File file = sourceProvider.getManifestFile();
            collection.add(file);
        }

        List listP = (List) collection;
        GPathResult result = scarper.parse((File) listP.get(0));
        return result.getProperty("@package").toString()
    }

}
