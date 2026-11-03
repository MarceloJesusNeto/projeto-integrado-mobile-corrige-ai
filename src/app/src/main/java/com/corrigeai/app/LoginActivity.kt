package com.corrigeai.app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.corrigeai.app.data.PasswordUtils
import com.corrigeai.app.data.UserRepository
import com.corrigeai.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userRepository = UserRepository()
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoToRegisterText()

        binding.togglePassword.setOnClickListener { togglePasswordVisibility() }

        binding.loginButton.setOnClickListener { attemptLogin() }

        binding.goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            Toast.makeText(this, "Funcionalidade em breve", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGoToRegisterText() {
        binding.goToRegister.text = buildString {
            append(getString(R.string.no_account))
            append(getString(R.string.create_now))
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        binding.passwordInput.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.passwordInput.setSelection(binding.passwordInput.text.length)
    }

    private fun attemptLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loginButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val user = userRepository.findByEmail(email)
                if (user == null) {
                    Toast.makeText(this@LoginActivity, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val valid = PasswordUtils.verifyPassword(password, user.salt, user.passwordHash)
                if (valid) {
                    Toast.makeText(this@LoginActivity, "Bem-vindo, ${user.fullName}!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra(HomeActivity.EXTRA_USER_NAME, user.fullName)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Senha incorreta", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Não foi possível conectar ao MySQL. Confira o IP/porta em DbConfig e se o servidor está no ar.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.loginButton.isEnabled = true
            }
        }
    }
}
