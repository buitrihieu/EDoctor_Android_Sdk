package vn.edoctor.sdk_edoctor

import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class EDoctorWebView : DialogFragment() {
    private lateinit var loading: ConstraintLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var myWebView: WebView
    private var buttonBack: Button? = null
    private var buttonNext: Button? = null
    private lateinit var buttonClose: ImageView
    lateinit var containerErrorNetwork: ConstraintLayout
    lateinit var header: ConstraintLayout
    private var checkTimeoutLoadWebView = false
    val domain = "https://e-doctor.dev/pk/dai-ichi-life"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EDoctorWebView.isVisible = true
        setStyle(STYLE_NO_FRAME, R.style.DialogStyle);
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isVisible) return
        super.show(manager, tag)
    }

    companion object {
        var isVisible = false
    }

    private fun backScreen(): Unit {
        dismiss()
    }

    private fun isNetworkConnected(): Boolean {
        val cm =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(
            R.layout.webview,
            container, false
        )
        WebView(requireContext()).clearCache(true)
        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        dialog?.window?.setStatusBarColor(Color.TRANSPARENT);
        myWebView = v.findViewById(R.id.webview)
        loadingProgressBar = v.findViewById(R.id.loadingProgressBar)
        loading = v.findViewById(R.id.loading)
        buttonBack = v.findViewById(R.id.buttonBack)
        buttonNext = v.findViewById(R.id.buttonNext)
        header = v.findViewById(R.id.header)
        buttonClose = v.findViewById(R.id.buttonClose)
        containerErrorNetwork = v.findViewById(R.id.containerErrorNetwork)
        myWebView.clearCache(true);
        myWebView.clearFormData();
        myWebView.clearHistory();
        myWebView.clearSslPreferences();
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        buttonBack?.setOnClickListener {
            dismiss()
        }
        buttonNext?.setOnClickListener {
            containerErrorNetwork.visibility = View.GONE
            loading.visibility = View.VISIBLE
            myWebView.reload()
        }
        myWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                if (errorCode == -2) {
                    requireActivity().runOnUiThread {
                        loading.visibility = View.GONE
                        containerErrorNetwork?.visibility = View.VISIBLE
                    }
                }
                super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (loading.visibility != View.GONE) {
                    loading.visibility = View.GONE
                }
                checkTimeoutLoadWebView = true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Thread {
                    try {
                        Thread.sleep(30000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (!checkTimeoutLoadWebView) {
                        if (isVisible) {
                            requireActivity().runOnUiThread {
                                loading.visibility = View.GONE
                                containerErrorNetwork.visibility = View.VISIBLE
                            }
                        }
                    }
                }.start()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString()
                return if (url == null || url.startsWith("http://") || url.startsWith("https://")) false else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                    view!!.context.startActivity(intent)
                    true
                } catch (e: Exception) {
                    Log.d(EDoctor.LOG_TAG, "shouldOverrideUrlLoading Exception:$e")
                    true
                }
            }
        }

        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.setSupportMultipleWindows(true)
        webSettings.setGeolocationEnabled(true)
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.databaseEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.allowContentAccess = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.setGeolocationEnabled(true)
        webSettings.loadWithOverviewMode = true
        webSettings.allowFileAccess = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
//        myWebView.addJavascriptInterface(jsObject, "messageHandlers")

        cookieManager.setAcceptThirdPartyCookies(myWebView, true)
        buttonClose.setOnClickListener {
            header.visibility = View.GONE
            myWebView.loadUrl(domain)
        }
        myWebView.loadUrl(domain)
        if (!isNetworkConnected()) {
            containerErrorNetwork.visibility = View.VISIBLE
        }
        return v
    }


    override fun onDestroy() {
        myWebView.removeAllViews();
        myWebView.destroy()
        EDoctorWebView.isVisible = false
        super.onDestroy()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                if (myWebView.url == domain) {
                    dismiss()
                } else if (myWebView.canGoBack()) {
                    myWebView.goBack()
                } else {
                    dismiss()
                }
            }
        }
    }
}
