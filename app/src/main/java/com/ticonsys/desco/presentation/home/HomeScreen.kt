package com.ticonsys.desco.presentation.home

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ticonsys.desco.R

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


    Box(modifier = Modifier.fillMaxSize()) {
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
                loadUrl(url)
            }
        }, update = {
            it.loadUrl(url)
        })

//        if(hasSubmitted){
//            IconButton(
//                onClick = {
//                    webView?.visibility = View.GONE
//                    viewModel.submit(false)
//                },
//                modifier = Modifier.align(alignment = Alignment.TopEnd)
//            ) {
//                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
//            }
//        }
    }

    if (!hasSubmitted) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = "http://prepaid.desco.org.bd/landing-img/desco-logo.png",
                    contentDescription = "Desco Image"
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "You can check your balance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = viewModel::updateAccountNumber,
                    label = {
                        Text(text = "Account/Meter No")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Person")
                    },
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

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
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(16.dp),
                text = "* Unofficial app",
                fontSize = 12.sp,
                fontWeight = FontWeight.W300
            )
        }

    }


}