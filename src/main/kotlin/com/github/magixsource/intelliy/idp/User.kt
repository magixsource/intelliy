package com.github.magixsource.intelliy.idp

data class User(
    val id: Int,
    val email: String,
    val realName: String,
    val loginName: String,
    val organizationId: String,
    val organizationCode: String,
    val enabled: Boolean

)
