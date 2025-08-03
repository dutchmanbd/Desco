package com.ticonsys.desco.presentation.home

import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class DescoWebClient(
    val onLoading: (Boolean) -> Unit = {},
    val onUrlChanged: (String) -> Unit = {}
) : WebViewClient() {

    private val historyUrls = mutableListOf<String>()

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onLoading(true)
//        url?.let { newUrl ->
//            onUrlChanged(url)
//        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(
            "document.querySelector('.card-group').style.display = 'none'"
        ) {}
        onLoading(false)
        url?.let { newUrl ->
            if (!historyUrls.contains(newUrl)) {
                historyUrls.add(newUrl)
                onUrlChanged(newUrl)
            }
        }
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        url?.let { newUrl ->
            if (!historyUrls.contains(newUrl)) {
                historyUrls.add(newUrl)
                onUrlChanged(newUrl)
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        Log.e(TAG, "shouldOverrideUrlLoading: $url")
//        url?.let { newUrl ->
//            view?.loadUrl(newUrl)
//            onUrlChanged(newUrl)
//        }
        return false
    }


    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        onLoading(false)
        Log.e(TAG, "onReceivedError: ${error?.description}")
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        onLoading(false)
        Log.e(TAG, "onReceivedHttpError: ${errorResponse?.statusCode}")
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        onLoading(false)
        Log.e(TAG, "onReceivedSslError: ${error}")
    }

    companion object {
        private const val TAG = "DescoWebClient"
    }
}