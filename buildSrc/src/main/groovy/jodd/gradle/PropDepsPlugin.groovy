package jodd.gradle

import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.UnionFileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.*
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * Plugin to allow 'optional' and 'provided' dependency configurations.
 */
class PropDepsPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.plugins.apply(JavaPlugin)

		def provided = addConfiguration(project, "provided")
		def optional = addConfiguration(project, "optional")

		JavaPluginConvention javaConvention = project.convention.plugins["java"]
		SourceSetContainer sourceSets = javaConvention.sourceSets
		addToSourceSet(sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME), provided, optional)
		addToSourceSet(sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME), provided, optional)

		Javadoc javadoc = project.tasks.getByName(JavaPlugin.JAVADOC_TASK_NAME)
		javadoc.classpath = new UnionFileCollection(javadoc.classpath, provided, optional)

		project.configurations.getByName("testRuntime").extendsFrom(provided, optional)
	}

	private Configuration addConfiguration(Project project, String name) {
		Configuration configuration = project.configurations.add(name)
		configuration.visible = false
		configuration.extendsFrom(project.configurations.getByName("compile"))
		return configuration
	}

	private addToSourceSet(SourceSet sourceSet, FileCollection... configurations) {
		sourceSet.compileClasspath = new UnionFileCollection(
			[sourceSet.compileClasspath] + configurations)
		sourceSet.runtimeClasspath = new UnionFileCollection(
			[sourceSet.runtimeClasspath] + configurations)
	}
}