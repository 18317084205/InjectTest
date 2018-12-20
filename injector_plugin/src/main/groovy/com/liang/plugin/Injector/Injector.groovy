package com.liang.plugin.Injector

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.android.builder.model.SourceProvider
import groovy.util.slurpersupport.GPathResult
import kotlin.io.FilesKt
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.concurrent.atomic.AtomicBoolean

class Injector implements Plugin<Project> {
    def versions = "1.0.4"
    @Override
    void apply(Project project) {

        print("apply project ...")

        if (!project.android) {
            throw new IllegalStateException("'android-application' or 'android-library' plugin required.")
        }

        def isApp = project.plugins.withType(AppPlugin)
        def isLib = project.plugins.withType(LibraryPlugin)
        if (!isApp && !isLib) {
            throw new IllegalStateException("'android-application' or 'android-library' plugin required.")
        }

        if (isLib) {
            def variants = project.android.libraryVariants
            configureR2Generation(project, variants)
        }


        project.dependencies {
            implementation "org.liang:injector:${versions}"
            implementation "org.liang:injector_annotations:${versions}"
            annotationProcessor "org.liang:injector_complier:${versions}"
        }
    }

    void configureR2Generation(Project project, DomainObjectSet<BaseVariant> variants) {
        variants.all { variant ->
            File buildDir = project.getBuildDir();
            StringBuilder stringBuilder = (new StringBuilder()).append("generated/source/r2/");
            File outputDir = FilesKt.resolve(buildDir, stringBuilder.append(variant.getDirName()).toString());
            String packageName = getPackageName(variant);
            AtomicBoolean atomicBoolean = new AtomicBoolean();

            variant.outputs.all { output ->
                ProcessAndroidResources processResources = output.processResources
                if (atomicBoolean.compareAndSet(false, true)) {
                    def file
                    if (processResources instanceof GenerateLibraryRFileTask) {
                        file = ((GenerateLibraryRFileTask) processResources).getTextSymbolOutputFile()
                    } else if (processResources instanceof LinkApplicationAndroidResourcesTask) {
                        file = ((LinkApplicationAndroidResourcesTask) processResources).getTextSymbolOutputFile()
                    } else {
                        throw (Throwable) (new RuntimeException("Minimum supported Android Gradle Plugin is 3.1.0"))
                    }
                    def injectorRFile = project.files(file).builtBy(processResources)

                    project.tasks.create("generate${variant.name.capitalize()}R2", R2Generator.class, new Action<R2Generator>() {
                        @Override
                        void execute(R2Generator generator) {
                            generator.outputDir = outputDir
                            generator.fileCollection = injectorRFile
                            generator.packageName = packageName
                            generator.className = "R2"
                            variant.registerJavaGeneratingTask(generator, outputDir)
                        }
                    })

                }
            }
        }
    }

    String getPackageName(BaseVariant variant) {
        XmlSlurper slurper = new XmlSlurper(false, false)
        List list = variant.getSourceSets()
        Iterable iterable = (Iterable)list
        Collection collection = new ArrayList()
        Iterator iterator = iterable.iterator()

        while(iterator.hasNext()) {
            Object obj = iterator.next()
            SourceProvider sourceProvider = (SourceProvider)obj
            File file = sourceProvider.getManifestFile();
            collection.add(file);
        }

        List listP = (List)collection;
        GPathResult result = slurper.parse((File)listP.get(0));
        return result.getProperty("@package").toString()
    }

}
