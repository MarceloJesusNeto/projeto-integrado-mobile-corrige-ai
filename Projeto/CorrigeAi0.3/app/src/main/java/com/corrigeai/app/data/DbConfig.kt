package com.corrigeai.app.data

/**
 * Dados de conexão com o MySQL.
 *
 * ATENÇÃO (protótipo/acadêmico):
 * Conectar direto do app Android ao MySQL, com usuário/senha embutidos no
 * app, NÃO é seguro para produção — qualquer pessoa pode fazer engenharia
 * reversa do APK e descobrir as credenciais do banco. O certo em produção
 * é ter uma API (Node, PHP, Java...) entre o app e o banco. Aqui está
 * simplificado assim de propósito, pois foi o que você pediu para o
 * protótipo.
 *
 * - Se o MySQL estiver rodando no SEU PC e você testar no EMULADOR do
 *   Android Studio, use HOST = "10.0.2.2" (é o "localhost" do seu PC
 *   visto de dentro do emulador).
 * - Se for testar em um CELULAR FÍSICO, use o IP da sua máquina na rede
 *   local (ex.: "192.168.0.10") e garanta que o celular está na mesma
 *   rede Wi-Fi do PC.
 * - Garanta que o MySQL aceita conexões externas (bind-address não pode
 *   estar preso a 127.0.0.1) e que o usuário abaixo tem permissão para
 *   conectar de fora do "localhost" (ex.: 'corrigeai_user'@'%').
 */
object DbConfig {
    const val HOST = "10.0.2.2"       // troque pelo IP do seu servidor MySQL
    const val PORT = 3306
    const val DATABASE = "corrige_ai"
    const val USER = "corrigeai_user"
    const val PASSWORD = "troque_esta_senha"

    val JDBC_URL: String
        get() = "jdbc:mysql://$HOST:$PORT/$DATABASE?useSSL=false&characterEncoding=UTF-8&connectTimeout=5000"
}
