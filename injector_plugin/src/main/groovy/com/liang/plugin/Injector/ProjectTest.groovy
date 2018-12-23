package com.liang.plugin.Injector

import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler

public class ProjectTest {
    public static void test(Project project) {
        print("test project name..." + project.name + "\n")
        print("test project plugins..." + project.plugins.size() + "\n")
        print("test project hasPlugin..." + project.pluginManager.hasPlugin('kotlin-android') + "\n")
        print("test project parent_plugins..." + project.parent.plugins.size() + "\n")
        print("test project parent_name..." + project.parent.name + "\n")
        print("test project parent_dependencies..." + project.parent.dependencies.metaPropertyValues.size() + "\n")
        print("test project parent_properties..." + project.parent.dependencies.properties.size() + "\n")
        print("test project parent_artifactTypes..." + project.parent.dependencies.artifactTypes.size() + "\n")
        print("test project attributesSchema_properties..." + project.parent.dependencies.attributesSchema.properties.size() + "\n")
        print("test project attributesSchema_metaPropertyValues..." + project.parent.dependencies.attributesSchema.metaPropertyValues.size() + "\n")




        project.logger.error("test project sourceURI..." + project.parent.buildscript.configurations.getByName(ScriptHandler.CLASSPATH_CONFIGURATION) + "\n")

        def propertyValues = project.parent.buildscript.configurations.getByName(ScriptHandler.CLASSPATH_CONFIGURATION).metaPropertyValues
        propertyValues.each {
            print("test project PropertyValue..." + it.name + "\n")
        }


        def objectMap = project.parent.buildscript.configurations.getByName(ScriptHandler.CLASSPATH_CONFIGURATION).properties

        objectMap.each {
            print("test project objectMap..." + it.key + "->" + String.valueOf(it.value) + "\n")
        }

        def rules = project.parent.buildscript.configurations.getByName(ScriptHandler.CLASSPATH_CONFIGURATION).dependencies
        rules.each {
            print("test project artifactTypeContainer..." + it.name + "\n")
            it.metaPropertyValues.each {
                print("artifactTypeContainer_metaPropertyValue..." + it.name + "\n")
            }

            it.properties.each {
                print("artifactTypeContainer_objectMap..." + it.key + "->" + it.value + "\n")
            }

        }


        def attributesSchema =project.parent.configurations.getByName(ScriptHandler.CLASSPATH_CONFIGURATION).attributes

        print("attributesSchema_dump..." + attributesSchema.dump() + "\n")

        attributesSchema.properties.each {
            print("attributesSchema_objectMap..." + it.key + "->" + it.value + "\n")
        }
        attributesSchema.metaPropertyValues.each {
            print("attributesSchema_metaPropertyValue..." + it.name + "\n")
        }

        attributesSchema.attributes.each {
            print("attributesSchema_attributes..." + it.name + "\n")
        }

    }

}
