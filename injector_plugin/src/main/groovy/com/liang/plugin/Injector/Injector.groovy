package com.liang.plugin.Injector

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class Injector implements Plugin<Project> {
    def versions = "1.0.2"

    @Override
    void apply(Project project) {
        if (!project.android) {
            throw new IllegalStateException("'android-application' or 'android-library' plugin required.")
        }

        def isApp = project.plugins.withType(AppPlugin)
        def isLib = project.plugins.withType(LibraryPlugin)
        if (!isApp && !isLib) {
            throw new IllegalStateException("'android-application' or 'android-library' plugin required.")
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
