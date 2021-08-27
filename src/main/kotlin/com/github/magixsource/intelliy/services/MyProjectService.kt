package com.github.magixsource.intelliy.services

import com.github.magixsource.intelliy.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
