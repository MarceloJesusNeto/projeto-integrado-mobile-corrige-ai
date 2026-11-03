package com.corrigeai.app.data

/**
 * Representa um usuário cadastrado no MySQL.
 * A senha nunca é guardada em texto puro — só o hash + salt.
 */
data class User(
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val salt: String
)
