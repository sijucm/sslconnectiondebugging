//Gradle task to get current version of the project.

task getVersion(type: VersionTask)
class VersionTask extends DefaultTask {
    @TaskAction
    def version() {
        print("##vso[task.setvariable variable=stpl_application_version]"+project.version)
    }
}

jacoco {
    toolVersion = "0.8.7"
}
test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir('jacocoHtml')
    }
}
