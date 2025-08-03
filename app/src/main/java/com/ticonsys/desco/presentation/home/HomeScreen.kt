package com.ticonsys.desco.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isSubmitted) {
        DescoScreen(uiState){

        }
    } else {
        AccountNumberForm(
            accountNumber = uiState.accountNumber,
            onAccountNumberChange = viewModel::updateAccountNumber,
            onSubmit = viewModel::saveAccountNumber
        )
    }
}


@Composable
fun AccountNumberForm(
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = accountNumber,
            onValueChange = onAccountNumberChange,
            placeholder = {
                Text(
                    text = "Account Number"
                )
            }
        )

        Button(
            onClick = onSubmit,
            enabled = accountNumber.isNotEmpty()
        ) {
            Text(text = "Submit")
        }
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DescoScreen(
    uiState: HomeUiState,
    onBackPress: () -> Unit
) {

//    val url = "http://prepaid.desco.org.bd/customer/#/customer-login"
    val url = "https://prepaid.desco.org.bd/customer/#/customer-info"

    var webView by remember { mutableStateOf<WebView?>(null) }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val client = remember {
        DescoWebClient(
            onLoading = {
                isLoading = it
            }
        ) { updatedUrl ->
            Log.e("DocumentView", "WebDocumentReader: $updatedUrl, ${uiState.accountNumber}")
            if(updatedUrl.contains("/#/customer-login")){
                webView?.evaluateJavascript(
                    """
                                   el= document.querySelector('[placeholder="Account/Meter No"]')
                                   el.value = '${uiState.accountNumber}'

                                   var e = document.createEvent('HTMLEvents');
                                   e.initEvent('input', true, true);
                                   el.dispatchEvent(e)

                                   document.querySelector('form button').click()
                                """.trimIndent()
                ) {

                }
                webView?.visibility = View.VISIBLE
            } else {
                webView?.visibility = View.VISIBLE
            }
        }
    }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBackPress()
        }

    }

    AndroidView(
        factory = {
            WebView(it).apply {
                webView = this
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = client
                settings.apply {
                    javaScriptEnabled = true
//                    javaScriptCanOpenWindowsAutomatically = true
                    domStorageEnabled = true
                    // Use Chrome's user agent
                    userAgentString =
                        "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    allowFileAccess = true
                    allowContentAccess = true
                    databaseEnabled = true
                }
//                webViewClient = object : WebViewClient() {
//                    override fun onPageFinished(view: WebView?, url: String?) {
//                        super.onPageFinished(view, url)
//                        view?.evaluateJavascript(
//                            "document.querySelector('.card-group').style.display = 'none'"
//                        ) {}
//                    }
//                }
                loadUrl(url)


            }
        },
        update = {
            it.loadUrl(url)
        },
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    )

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

}