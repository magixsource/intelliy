package com.github.magixsource.intelliy.idp

data class Instance(val id: Int, val code: String, val appId: Int, val envId: Int, val status: String) {
    override fun toString(): String {
        return this.code
    }
}
