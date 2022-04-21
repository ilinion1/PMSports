package com.pmspot.plius.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.pmspot.plius.R
import com.pmspot.plius.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebViewBinding
    private var fileData: ValueCallback<Uri>? = null
    private var filePath: ValueCallback<Array<Uri>>? = null
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra("link")?.let { link = it }
        startWebView() //запускаю WebView
        startResultLauncher() // запускаю лаунчер, для обработки данных с хранилища(картинок)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView() = with(binding) {
        webViewId.loadUrl(link)
        webViewId.settings.javaScriptEnabled = true
        webViewId.settings.domStorageEnabled = true
        webViewId.settings.loadWithOverviewMode = true
        webViewId.clearCache(false)
        webViewId.settings.cacheMode = WebSettings.LOAD_DEFAULT
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webViewId, true)
        webViewId.webChromeClient = ChromeClient()
        webViewId.webViewClient = WebViewClient()
    }



    private inner class ChromeClient : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            filePath = filePathCallback
            launchResult()
            return true
        }
    }


    private fun launchResult() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startForResult.launch(i)
    }

    private fun startResultLauncher() {
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (fileData == null && filePath == null) return@registerForActivityResult
                val resultFileData: Uri?
                val resultsFilePath: Array<Uri>?
                if (result.data == null) {
                    resultFileData = null
                    resultsFilePath = null
                } else {
                    resultFileData = result.data?.data
                    resultsFilePath = arrayOf(Uri.parse(result.data?.dataString))
                }
                fileData?.onReceiveValue(resultFileData)
                filePath?.onReceiveValue(resultsFilePath)
            }
    }

    override fun onBackPressed() {
        if (binding.webViewId.canGoBack()) {
            binding.webViewId.goBack()
        } else {
            return
        }
    }
}