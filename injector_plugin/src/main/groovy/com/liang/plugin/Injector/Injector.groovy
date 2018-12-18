package com.liang.plugin.Injector

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.android.builder.model.SourceProvider
import groovy.util.slurpersupport.GPathResult
import kotlin.collections.CollectionsKt
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer

import java.util.concurrent.atomic.AtomicBoolean

class Injector implements Plugin<Project> {
    def versions = "1.0.2"

    String getPackageName(BaseVariant variant) {
        XmlSlurper slurper = XmlSlurper(false, false)
        List list = variant.getSourceSets()
        Iterable iterable = (Iterable)list;
        Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));
        Iterator iterator = iterable.iterator();

        while(iterator.hasNext()) {
            Object obj = iterator.next();
            SourceProvider sourceProvider = (SourceProvider)obj
            File file = sourceProvider.getManifestFile();
            collection.add(file);
        }

        List listP = (List)collection;
        GPathResult result = slurper.parse((File)listP.get(0));
        return result.getProperty("@package").toString()
    }


    void configureR2Generation(Project project, DomainObjectSet<BaseVariant> variants) {
        variants.all { variant ->
            String string = (String)project.findProperty("android.useAndroidX");
            boolean useAndroidX = string != null?Boolean.parseBoolean(string):false

            File file1 = project.getBuildDir();
            StringBuilder stringBuilder = (new StringBuilder()).append("generated/source/r2/");
            final File outputDir = FilesKt.resolve(file1, stringBuilder.append(variant.getDirName()).toString());
            final String rPackage = getPackageName(variant);
            final AtomicBoolean once = new AtomicBoolean();


            print("configureR2Generation useAndroidX ..." + useAndroidX)
            print("configureR2Generation outputDir ..." + outputDir)
            print("configureR2Generation rPackage ..." + rPackage)

            variant.outputs.all { output ->
                ProcessAndroidResources processResources = output.processResources

                // Though there might be multiple outputs, their R files are all the same. Thus, we only
                // need to configure the task once with the R.java input and action.
                if (once.compareAndSet(false, true)) {
                    def file
                    def rFile
                    if (processResources instanceof GenerateLibraryRFileTask) {
                        file = ((GenerateLibraryRFileTask) processResources).getTextSymbolOutputFile()
                    } else if (processResources instanceof LinkApplicationAndroidResourcesTask) {
                        file = ((LinkApplicationAndroidResourcesTask) processResources).getTextSymbolOutputFile()
                    } else {
                        throw (Throwable) (new RuntimeException("Minimum supported Android Gradle Plugin is 3.1.0"))
                    }
                    rFile = project.files(file).builtBy(processResources)
                    println("configureR2Generation fileCollection ..." + rFile.toString())
                    project.tasks.create("generate${variant.name.capitalize()}R2", R2Generator.class, new Action<R2Generator>() {
                        @Override
                        void execute(R2Generator generator) {
                            print("configureR2Generation it ..." + generator.className)
                            generator.outputDir = outputDir
                            generator.fileCollection = rFile
                            generator.useAndroidX = useAndroidX
                            generator.packageName = rPackage
                            generator.className = "R2"
                            variant.registerJavaGeneratingTask(generator, outputDir)
                        }
                    })

                }
            }
        }
    }

    private ExtensionContainer get(ExtensionContainer container, Class type) {
        return container.getByType(type)
    }

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
            print("apply isLib ...")
            BaseExtension libraryExtension = ((LibraryPlugin) project.plugins.getPlugin(LibraryPlugin)).getExtension()
            print("apply libraryExtension ..." + libraryExtension.toString())
            configureR2Generation(project, ((LibraryExtension) libraryExtension).getLibraryVariants())
        }


        project.dependencies {
            // TODO this should come transitively
            if (isApp) {
                implementation "org.liang:injector:${versions}"
                print("org.liang:injector_annotations:${versions} ...")
                implementation "org.liang:injector_annotations:${versions}"
                print("org.liang:injector_annotations:${versions} ...")
                annotationProcessor "org.liang:injector_complier:${versions}"
                print("org.liang:injector_complier:${versions} ...")
            }
        }
    }
}
