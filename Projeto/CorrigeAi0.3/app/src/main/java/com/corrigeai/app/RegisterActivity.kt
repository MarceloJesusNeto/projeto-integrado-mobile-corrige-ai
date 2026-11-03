package com.corrigeai.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.corrigeai.app.data.PasswordUtils
import com.corrigeai.app.data.User
import com.corrigeai.app.data.UserRepository
import com.corrigeai.app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoToLoginText()
        setupTermsText()
        updateCreateButtonState()

        binding.backButton.setOnClickListener { finish() }
        binding.goToLogin.setOnClickListener { finish() }

        binding.termsCheckbox.setOnCheckedChangeListener { _, _ -> updateCreateButtonState() }

        binding.createAccountButton.setOnClickListener { attemptRegister() }
    }

    private fun setupGoToLoginText() {
        binding.goToLogin.text = buildString {
            append(getString(R.string.already_have_account))
            append(getString(R.string.login_link))
        }
    }

    private fun setupTermsText() {
        binding.termsText.text = buildString {
            append(getString(R.string.agree_terms_prefix))
            append(getString(R.string.terms_of_use))
            append(getString(R.string.and_word))
            append(getString(R.string.privacy_policy))
        }
    }

    private fun updateCreateButtonState() {
        val enabled = binding.termsCheckbox.isChecked
        binding.createAccountButton.isEnabled = enabled
        binding.createAccountButton.setBackgroundResource(
            if (enabled) R.drawable.bg_button_primary else R.drawable.bg_button_disabled
        )
        binding.createAccountButton.setTextColor(
            ContextCompat.getColor(this, if (enabled) R.color.black else R.color.text_hint)
        )
    }

    private fun attemptRegister() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 8) {
            Toast.makeText(this, "A senha deve ter no mínimo 8 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }

        if (!binding.termsCheckbox.isChecked) {
            Toast.makeText(this, "Você precisa aceitar os termos", Toast.LENGTH_SHORT).show()
            return
        }

        binding.createAccountButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val existing = userRepository.countByEmail(email)
                if (existing > 0) {
                    Toast.makeText(this@RegisterActivity, "Este e-mail já está cadastrado", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val salt = PasswordUtils.generateSalt()
                val hash = PasswordUtils.hashPassword(password, salt)

                val user = User(
                    fullName = name,
                    email = email,
                    passwordHash = hash,
                    salt = salt
                )

                userRepository.insertUser(user)

                Toast.makeText(this@RegisterActivity, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                intent.putExtra(HomeActivity.EXTRA_USER_NAME, name)
                startActivity(intent)
                finishAffinity()
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Não foi possível conectar ao MySQL. Confira o IP/porta em DbConfig e se o servidor está no ar.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                updateCreateButtonState()
            }
        }
    }
}
