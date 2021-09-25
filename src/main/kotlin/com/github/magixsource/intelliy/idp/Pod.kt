package com.github.magixsource.intelliy.idp

data class Pod(
    val id: Int,
    val clusterId: Int,
    val connect: Boolean,
    val envCode: String,
    val envId: Int,
    val instanceCode: String,
    val name: String,
    val status: String
) {
    override fun toString(): String {
        return name
    }
}
