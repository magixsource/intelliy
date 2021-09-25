package com.github.magixsource.intelliy.idp


data class Project(val id: Int, val code: String, val name: String) {
    override fun toString(): String {
        return name
    }
}
