package com.ticonsys.desco.presentation.home

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()

    val hasSubmitted by remember {
        viewModel.hasSubmitted
    }


    val accountNumber by remember {
        viewModel.accountNumber
    }
    val url = "http://prepaid.desco.org.bd/customer/#/customer-login"

    var webView: WebView? = null


    AndroidView(factory = {
        WebView(it).apply {
            webView = this
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
//                    view?.evaluateJavascript(
//                        "document.querySelector('form button').style.display = 'none'"
//                    ) {}
                    view?.evaluateJavascript(
                        "document.querySelector('.card-group').style.display = 'none'"
                    ) {}
                }
            }
            visibility = View.GONE
        }
    }, update = {
        it.loadUrl(url)
    })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (!hasSubmitted) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = accountNumber,
                    onValueChange = viewModel::updateAccountNumber
                )

                Button(
                    onClick = {
                        webView?.evaluateJavascript(
                            """
                                   el= document.querySelector('[placeholder="Account/Meter No"]')
                                   el.value = '$accountNumber'

                                   var e = document.createEvent('HTMLEvents');
                                   e.initEvent('input', true, true);
                                   el.dispatchEvent(e)

                                   document.querySelector('form button').click()
                                """.trimIndent()
                        ) {

                        }

                        viewModel.submit(true)
                        webView?.visibility = View.VISIBLE
                    },
                    enabled = accountNumber.isNotEmpty()
                ) {
                    Text(text = "Submit")
                }
            }
        }


    }

}