package com.corrigeai.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

/**
 * Acesso direto ao MySQL via JDBC.
 * Todas as funções são "suspend" e rodam em Dispatchers.IO, porque
 * chamada de rede/banco nunca pode travar a thread principal (UI).
 */
class UserRepository {

    private fun getConnection(): Connection {
        // Nome da classe do driver 5.1.x (sem o "cj" da versão 8.x)
        Class.forName("com.mysql.jdbc.Driver")
        return DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USER, DbConfig.PASSWORD)
    }

    suspend fun countByEmail(email: String): Int = withContext(Dispatchers.IO) {
        getConnection().use { conn ->
            conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?").use { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        }
    }

    suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        getConnection().use { conn ->
            conn.prepareStatement("SELECT id, full_name, email, password_hash, salt FROM users WHERE email = ? LIMIT 1").use { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        User(
                            id = rs.getLong("id"),
                            fullName = rs.getString("full_name"),
                            email = rs.getString("email"),
                            passwordHash = rs.getString("password_hash"),
                            salt = rs.getString("salt")
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO) {
        getConnection().use { conn ->
            conn.prepareStatement(
                "INSERT INTO users (full_name, email, password_hash, salt) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { stmt ->
                stmt.setString(1, user.fullName)
                stmt.setString(2, user.email)
                stmt.setString(3, user.passwordHash)
                stmt.setString(4, user.salt)
                stmt.executeUpdate()
                stmt.generatedKeys.use { keys ->
                    if (keys.next()) keys.getLong(1) else -1L
                }
            }
        }
    }

    companion object {
        private const val TAG = "UserRepository"

        /** Testa a conexão — útil para dar uma mensagem clara se o MySQL não responder. */
        suspend fun testConnection(): Result<Unit> = withContext(Dispatchers.IO) {
            try {
                Class.forName("com.mysql.jdbc.Driver")
                DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USER, DbConfig.PASSWORD).close()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao conectar no MySQL", e)
                Result.failure(e)
            }
        }
    }
}
