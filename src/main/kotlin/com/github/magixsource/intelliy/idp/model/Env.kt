package com.github.magixsource.intelliy.idp.model

data class Env(val id: Int, val code: String, val name: String, val active: Boolean) {
    override fun toString(): String {
        return this.name
    }
}
