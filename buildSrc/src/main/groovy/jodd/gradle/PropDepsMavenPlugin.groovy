package jodd.gradle

import org.gradle.api.*
import org.gradle.api.artifacts.maven.Conf2ScopeMappingContainer
import org.gradle.api.artifacts.maven.MavenPom
import org.gradle.api.artifacts.maven.PomFilterContainer
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.*

/**
 * Plugin to allow optional and provided dependency configurations to work with the
 * standard gradle 'maven' plugin.
 */
class PropDepsMavenPlugin implements Plugin<Project> {

	public void apply(Project project) {
		project.plugins.apply(PropDepsPlugin)
		project.plugins.apply(MavenPlugin)

		Conf2ScopeMappingContainer scopeMappings = project.conf2ScopeMappings
		scopeMappings.addMapping(MavenPlugin.COMPILE_PRIORITY + 1,
			project.configurations.getByName("provided"), Conf2ScopeMappingContainer.PROVIDED)

		// Add a temporary new optional scope
		scopeMappings.addMapping(MavenPlugin.COMPILE_PRIORITY + 2,
			project.configurations.getByName("optional"), "optional")

		// Add a hook to replace the optional scope
		project.tasks.withType(Upload).each{ applyToUploadTask(project, it) }
	}

	private void applyToUploadTask(Project project, Upload upload) {
		upload.repositories.withType(PomFilterContainer).each{ applyToPom(project, it) }
	}

	private void applyToPom(Project project, PomFilterContainer pomContainer) {
		pomContainer.pom.whenConfigured{ MavenPom pom ->
			pom.dependencies.findAll{ it.scope == "optional" }.each {
				it.scope = "compile"
				it.optional = true
			}
		}
	}
}