-- Rode este script no seu servidor MySQL (Workbench, DBeaver, linha de comando, etc.)

CREATE DATABASE IF NOT EXISTS corrige_ai
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE corrige_ai;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    salt VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuário dedicado para o app se conectar (não use o root!).
-- O '%' permite conectar de qualquer host — troque pelo IP da rede local
-- se quiser restringir. Troque também a senha abaixo.
--
-- IMPORTANTE: "IDENTIFIED WITH mysql_native_password" é necessário porque
-- o driver JDBC 5.1.x (compatível com Android) não sabe conversar com o
-- método de autenticação padrão do MySQL 8 (caching_sha2_password).
CREATE USER IF NOT EXISTS 'corrigeai_user'@'%' IDENTIFIED WITH mysql_native_password BY 'troque_esta_senha';
GRANT SELECT, INSERT, UPDATE ON corrige_ai.* TO 'corrigeai_user'@'%';
FLUSH PRIVILEGES;
