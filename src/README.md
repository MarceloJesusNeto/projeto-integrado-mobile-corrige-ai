# Corrige Aí — Protótipo Android (com MySQL)

Protótipo em Android Studio com as telas de **Login** e **Criar Conta**, seguindo o design enviado, conectando **direto ao MySQL via JDBC** (sem backend no meio).

> ⚠️ **Aviso importante:** conectar o app Android direto no MySQL, com usuário/senha do banco embutidos no APK, **não é seguro para produção** — qualquer pessoa pode fazer engenharia reversa do app e pegar as credenciais do banco. Isso é aceitável para um protótipo/trabalho acadêmico rodando na sua rede local, mas para um app real o certo é ter uma API (Node, PHP, Java Spring...) entre o app e o banco. Deixei um aviso também no código, em `DbConfig.kt`.

## 1. Configurar o MySQL

1. Tenha um servidor MySQL rodando (local ou em uma máquina da sua rede).
2. Rode o script `schema.sql` (está na raiz do projeto) — ele cria o banco `corrige_ai`, a tabela `users` e um usuário `corrigeai_user` dedicado para o app.
3. (Opcional, mas útil) Rode também o `seed.sql` — ele insere um **usuário de exemplo já cadastrado**, para você testar a tela de Login sem precisar passar pelo Cadastro toda hora:
   - **E-mail:** `maria@exemplo.com`
   - **Senha:** `senha123`
4. **Troque a senha** `troque_esta_senha` no `schema.sql` (e depois no app, no passo 2).
5. Garanta que o MySQL aceita conexão de fora do `localhost`:
   - No `my.cnf`/`my.ini`, `bind-address` não pode estar travado em `127.0.0.1` (comente essa linha ou coloque `0.0.0.0`).
   - Reinicie o MySQL depois de mudar isso.
6. **Se você for usar o usuário `root`** em vez do `corrigeai_user` (não recomendado, mas funciona no protótipo), rode isto para trocar o método de autenticação dele para um que o driver do Android entende:
   ```sql
   ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'sua_senha_do_root';
   FLUSH PRIVILEGES;
   ```
   Se der erro dizendo que `'root'@'%'` não existe, rode primeiro `CREATE USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'sua_senha_do_root'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%'; FLUSH PRIVILEGES;`

> ⚠️ **Por que o `mysql_native_password`?** O driver JDBC compatível com Android (`mysql-connector-java` 5.1.x — explicado abaixo) não sabe conversar com o método de login padrão do MySQL 8 (`caching_sha2_password`). Sem essa troca, a conexão falha.

## 2. Configurar o app

Abra `app/src/main/java/com/corrigeai/app/data/DbConfig.kt` e ajuste:

```kotlin
const val HOST = "10.0.2.2"   // ver abaixo
const val PORT = 3306
const val DATABASE = "corrige_ai"
const val USER = "corrigeai_user"
const val PASSWORD = "a_senha_que_voce_definiu"
```

- **Testando no emulador do Android Studio** e o MySQL está no seu PC: use `HOST = "10.0.2.2"` (é como o emulador enxerga o `localhost` do seu computador).
- **Testando em celular físico**: use o IP da sua máquina na rede local (ex.: `192.168.0.10`, descubra com `ipconfig`/`ifconfig`) e deixe o celular na mesma rede Wi-Fi do PC. Confira também se o firewall do PC libera a porta 3306.

## 3. Abrir e rodar

1. Abra o Android Studio → `File > Open...` → selecione a pasta `CorrigeAi`.
2. Aguarde o **Gradle Sync** (baixa o driver do MySQL e as demais dependências — precisa de internet na primeira vez).
3. Rode no emulador/celular (▶️ Run 'app'). A tela inicial já é a de Login.

## ⚠️ Sobre o driver do MySQL no Android

O Android tem uma versão reduzida do `java.sql` — ela não inclui classes mais novas como `java.sql.SQLType`. Isso significa que **a versão mais recente do driver do MySQL (8.x) não funciona no Android** — o app crasha com `NoClassDefFoundError: Ljava/sql/SQLType`, sem nenhum aviso amigável na tela (é um `Error`, não uma `Exception`, então nem cai no tratamento de erro do app).

Por isso este projeto usa a versão **5.1.49** do driver (`mysql:mysql-connector-java:5.1.49`), que é mais antiga e compatível com o Android. A consequência é que o usuário do MySQL que o app usa precisa estar configurado com o método de autenticação `mysql_native_password` em vez do padrão `caching_sha2_password` do MySQL 8 (veja o passo 6 acima).

## O que foi implementado

- **`activity_login.xml`** / **`activity_register.xml`** / **`activity_home.xml`** — as telas do protótipo.
- **`data/DbConfig.kt`** — host, porta, banco, usuário e senha do MySQL (é aqui que você configura sua conexão).
- **`data/UserRepository.kt`** — todo o acesso ao banco via **JDBC puro** (`java.sql.DriverManager`), sempre em background (`Dispatchers.IO`) pra não travar a tela.
- **`data/User.kt`** — classe simples representando um usuário.
- **`data/PasswordUtils.kt`** — gera salt aleatório e hash SHA-256 da senha (a senha nunca é enviada nem salva em texto puro).
- **`schema.sql`** — script para criar o banco/tabela/usuário no MySQL.
- **`seed.sql`** — insere um usuário de exemplo (`maria@exemplo.com` / `senha123`) pronto para testar o login.

### Fluxo

- **Cadastro**: valida os campos, verifica se o e-mail já existe (`SELECT COUNT`), gera o hash da senha e faz o `INSERT` no MySQL.
- **Login**: busca o usuário por e-mail (`SELECT`) e confere o hash da senha localmente.
- Se o app não conseguir conectar no MySQL (IP errado, firewall, servidor fora do ar), aparece um Toast avisando — confira `DbConfig.kt` nesse caso.

## Próximos passos sugeridos

- Migrar para uma API própria entre o app e o MySQL (mais seguro).
- Trocar os botões Google/Apple visuais por login social real.
- Adicionar recuperação de senha de verdade.
- Usar HTTPS/TLS na conexão com o banco se ele estiver exposto fora da rede local.
