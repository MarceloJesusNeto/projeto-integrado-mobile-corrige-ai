-- Rode este script DEPOIS do schema.sql.
-- Ele insere um usuário de exemplo, já pronto para testar o LOGIN
-- sem precisar passar pela tela de cadastro.
--
-- Credenciais de teste:
--   E-mail: maria@exemplo.com
--   Senha:  senha123
--
-- (A senha nunca fica em texto puro no banco — abaixo já está salva
-- como hash SHA-256 + salt, do mesmo jeito que o app gera no cadastro.)

USE corrige_ai;

INSERT INTO users (full_name, email, password_hash, salt)
VALUES (
    'Maria Souza',
    'maria@exemplo.com',
    '600218935e41ad34e1b7da3be92f9680b66dfee97aac0a5b8f4450dc20574407',
    'a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6'
)
ON DUPLICATE KEY UPDATE full_name = full_name;
