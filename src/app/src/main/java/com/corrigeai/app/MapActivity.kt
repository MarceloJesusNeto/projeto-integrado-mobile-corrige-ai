package com.corrigeai.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.corrigeai.app.databinding.ActivityMapBinding

/**
 * Tela TEMPORÁRIA de mapa, mostrando trechos do DF com problemas de
 * iluminação/via. Usa OpenStreetMap (via Leaflet) dentro de um WebView,
 * então não precisa de chave de API do Google Maps para esse protótipo.
 *
 * Os pontos exibidos (assets/map.html) são dados de EXEMPLO — o próximo
 * passo é buscar essas coordenadas do banco (MySQL) em vez de um array
 * fixo no HTML.
 */
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        with(binding.mapWebView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        binding.mapWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.loadingSpinner.visibility = View.GONE
            }
        }

        // Carrega o mapa a partir de app/src/main/assets/map.html
        binding.mapWebView.loadUrl("file:///android_asset/map.html")
    }
}
